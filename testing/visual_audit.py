#!/usr/bin/env python3
import os
import sys
import time
import subprocess
import xml.etree.ElementTree as ET
import re

APP_PACKAGE = "com.easyui.guardianlauncher"
MAIN_ACTIVITY = "com.easyui.guardianlauncher.MainActivity"

# Output directories
SCREENSHOT_DIR = "artifacts/visual-audit/screenshots"
XML_DIR = "artifacts/visual-audit/xml"
LOG_DIR = "artifacts/visual-audit/logs"

# Ensure directories exist
os.makedirs(SCREENSHOT_DIR, exist_ok=True)
os.makedirs(XML_DIR, exist_ok=True)
os.makedirs(LOG_DIR, exist_ok=True)

# Logger setup
log_file_path = os.path.join(LOG_DIR, "audit_execution.log")
log_file = open(log_file_path, "w")

def log(msg):
    timestamp = time.strftime("%Y-%m-%d %H:%M:%S")
    formatted = f"[{timestamp}] {msg}"
    print(formatted)
    log_file.write(formatted + "\n")
    log_file.flush()

def run_cmd(cmd, shell=False):
    try:
        res = subprocess.run(cmd, shell=shell, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, timeout=15)
        return res.returncode, res.stdout.strip(), res.stderr.strip()
    except subprocess.TimeoutExpired:
        log(f"Timeout running command: {cmd}")
        return -1, "", "TIMEOUT"

def get_device_info():
    _, size_out, _ = run_cmd(["adb", "shell", "wm", "size"])
    _, density_out, _ = run_cmd(["adb", "shell", "wm", "density"])
    _, font_out, _ = run_cmd(["adb", "shell", "settings", "get", "system", "font_scale"])
    
    # Check current navbar mode
    _, navbar_out, _ = run_cmd("adb shell cmd overlay list | grep navbar", shell=True)
    current_nav = "threebutton" if "[x] com.android.internal.systemui.navbar.threebutton" in navbar_out else "gestural"
    
    # Parse size and density
    size_match = re.search(r'Physical size: (\d+x\d+)', size_out)
    if not size_match:
        size_match = re.search(r'Override size: (\d+x\d+)', size_out)
    density_match = re.search(r'Physical density: (\d+)', density_out)
    if not density_match:
        density_match = re.search(r'Override density: (\d+)', density_out)
        
    original_size = size_match.group(1) if size_match else "1080x2408"
    original_density = density_match.group(1) if density_match else "440"
    original_font = font_out.strip() if font_out.strip() else "1.3"
    
    return {
        "size": original_size,
        "density": original_density,
        "font_scale": original_font,
        "navbar": current_nav
    }

def apply_profile(profile_name, size, density, font_scale, navbar):
    log(f"Applying Profile {profile_name}: Size={size}, Density={density}, FontScale={font_scale}, Navbar={navbar}")
    
    # Apply resolution and density
    if size == "reset":
        run_cmd(["adb", "shell", "wm", "size", "reset"])
    else:
        run_cmd(["adb", "shell", "wm", "size", size])
        
    if density == "reset":
        run_cmd(["adb", "shell", "wm", "density", "reset"])
    else:
        run_cmd(["adb", "shell", "wm", "density", str(density)])
        
    # Apply font scale
    run_cmd(["adb", "shell", "settings", "put", "system", "font_scale", str(font_scale)])
    
    # Apply navbar overlay
    if navbar == "threebutton":
        run_cmd(["adb", "shell", "cmd", "overlay", "enable", "com.android.internal.systemui.navbar.threebutton"])
        run_cmd(["adb", "shell", "cmd", "overlay", "disable", "com.android.internal.systemui.navbar.gestural"])
    elif navbar == "gestural":
        run_cmd(["adb", "shell", "cmd", "overlay", "enable", "com.android.internal.systemui.navbar.gestural"])
        run_cmd(["adb", "shell", "cmd", "overlay", "disable", "com.android.internal.systemui.navbar.threebutton"])
        
    time.sleep(2) # Give the system some time to adjust layouts

def restore_device(defaults):
    log(f"Restoring original device settings: {defaults}")
    run_cmd(["adb", "shell", "wm", "size", defaults["size"]])
    run_cmd(["adb", "shell", "wm", "density", defaults["density"]])
    run_cmd(["adb", "shell", "settings", "put", "system", "font_scale", defaults["font_scale"]])
    
    if defaults["navbar"] == "threebutton":
        run_cmd(["adb", "shell", "cmd", "overlay", "enable", "com.android.internal.systemui.navbar.threebutton"])
        run_cmd(["adb", "shell", "cmd", "overlay", "disable", "com.android.internal.systemui.navbar.gestural"])
    else:
        run_cmd(["adb", "shell", "cmd", "overlay", "enable", "com.android.internal.systemui.navbar.gestural"])
        run_cmd(["adb", "shell", "cmd", "overlay", "disable", "com.android.internal.systemui.navbar.threebutton"])
        
    time.sleep(2)

def clean_and_launch():
    log("Clearing app data and launching MainActivity...")
    run_cmd(["adb", "shell", "pm", "clear", APP_PACKAGE])
    time.sleep(1)
    run_cmd(["adb", "shell", "am", "start", "-n", f"{APP_PACKAGE}/{MAIN_ACTIVITY}"])
    time.sleep(3) # Let app load

def capture_screenshot(profile, screen_name, suffix):
    filename = f"{profile}_{screen_name}_{suffix}.png"
    filepath = os.path.join(SCREENSHOT_DIR, filename)
    log(f"Capturing screenshot: {filename}")
    # run screencap
    _, out, err = run_cmd(f"adb exec-out screencap -p > {filepath}", shell=True)
    if err:
        log(f"Screenshot error: {err}")
    return filepath

def dump_xml(profile, screen_name):
    filename = f"{profile}_{screen_name}.xml"
    filepath = os.path.join(XML_DIR, filename)
    log(f"Dumping UIAutomator XML: {filename}")
    run_cmd(["adb", "shell", "uiautomator", "dump", "/sdcard/window.xml"])
    run_cmd(["adb", "pull", "/sdcard/window.xml", filepath])
    return filepath

def parse_bounds(bounds_str):
    m = re.match(r'\[(\d+),(\d+)\]\[(\d+),(\d+)\]', bounds_str)
    if m:
        return list(map(int, m.groups()))
    return None

def find_node_by_attrib(xml_path, attrib_name, attrib_val):
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        for node in root.iter('node'):
            if node.attrib.get(attrib_name) == attrib_val:
                return node
        return None
    except Exception as e:
        log(f"Error parsing XML: {e}")
        return None

def find_editText_nodes(xml_path):
    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        edit_texts = []
        for node in root.iter('node'):
            if node.attrib.get('class') == "android.widget.EditText":
                edit_texts.append(node)
        return edit_texts
    except Exception as e:
        log(f"Error parsing XML: {e}")
        return []

def get_node_center(node):
    bounds = node.attrib.get('bounds')
    if not bounds:
        return None
    coords = parse_bounds(bounds)
    if coords:
        x1, y1, x2, y2 = coords
        return (x1 + x2) // 2, (y1 + y2) // 2
    return None

def tap_node(node):
    center = get_node_center(node)
    if center:
        x, y = center
        log(f"Tapping node bounds: {node.attrib.get('bounds')} -> center: ({x}, {y})")
        run_cmd(["adb", "shell", "input", "tap", str(x), str(y)])
        time.sleep(1)
        return True
    return False

def tap_coordinates(x, y):
    log(f"Tapping coordinates ({x}, {y})")
    run_cmd(["adb", "shell", "input", "tap", str(x), str(y)])
    time.sleep(1)

def swipe_scroll(size_str):
    # Determine swipe coordinates based on resolution
    # size_str format: "widthxheight"
    try:
        w, h = map(int, size_str.split('x'))
    except Exception:
        w, h = 1080, 2408 # fallback
    x = w // 2
    y_start = int(h * 0.8)
    y_end = int(h * 0.2)
    log(f"Swiping up (scrolling down): ({x}, {y_start}) -> ({x}, {y_end})")
    run_cmd(["adb", "shell", "input", "swipe", str(x), str(y_start), str(x), str(y_end), "500"])
    time.sleep(1)

def type_text(text):
    log(f"Typing text: {text}")
    # Replace spaces with %s for adb
    adb_text = text.replace(" ", "%s")
    run_cmd(["adb", "shell", "input", "text", adb_text])
    time.sleep(0.5)

def run_audit_for_profile(profile_name, size, density, font_scale, navbar):
    log(f"\n==================== STARTING PROFILE {profile_name} ====================")
    apply_profile(profile_name, size, density, font_scale, navbar)
    
    # Get the current resolution after applying wm size (could be override)
    _, wm_size_out, _ = run_cmd(["adb", "shell", "wm", "size"])
    log(f"Active wm size output: {wm_size_out}")
    active_size = "412x915" # default guess
    m = re.search(r'Override size: (\d+x\d+)', wm_size_out)
    if not m:
        m = re.search(r'Physical size: (\d+x\d+)', wm_size_out)
    if m:
        active_size = m.group(1)
    
    clean_and_launch()
    
    # --- SCREEN 1: Welcome Step ---
    log("--- Screening Welcome Step ---")
    capture_screenshot(profile_name, "01_welcome", "before_scroll")
    xml_path = dump_xml(profile_name, "01_welcome")
    
    swipe_scroll(active_size)
    capture_screenshot(profile_name, "01_welcome", "after_scroll")
    
    # Find button "Get Started"
    btn = find_node_by_attrib(xml_path, "text", "Get Started")
    if btn is None:
        log("[ISSUE] 'Get Started' button not found in initial XML dump!")
        # Re-dump XML in case it only appeared after scroll
        xml_path = dump_xml(profile_name, "01_welcome_scrolled")
        btn = find_node_by_attrib(xml_path, "text", "Get Started")
        
    if btn is not None:
        log("Found 'Get Started' button. Tapping...")
        tap_node(btn)
    else:
        log("[CRITICAL] Cannot proceed from Welcome Step. 'Get Started' button completely unreachable.")
        return # Skip to next profile
        
    # --- SCREEN 2: PIN Setup Step ---
    log("--- Screening PIN Setup Step (Create PIN) ---")
    capture_screenshot(profile_name, "02_pin_setup", "before_scroll")
    xml_path = dump_xml(profile_name, "02_pin_setup")
    
    swipe_scroll(active_size)
    capture_screenshot(profile_name, "02_pin_setup", "after_scroll")
    
    # Tap PIN digits 1, 2, 3, 4
    # The keypad key "1"
    key1 = find_node_by_attrib(xml_path, "text", "1")
    key2 = find_node_by_attrib(xml_path, "text", "2")
    key3 = find_node_by_attrib(xml_path, "text", "3")
    key4 = find_node_by_attrib(xml_path, "text", "4")
    
    if key1 is not None and key2 is not None and key3 is not None and key4 is not None:
        log("Tapping digits 1, 2, 3, 4 on keypad...")
        tap_node(key1)
        tap_node(key2)
        tap_node(key3)
        tap_node(key4)
    else:
        log("[CRITICAL] Keypad digits not found in PIN setup!")
        return
        
    # Tap Continue button
    # The continue button has text "Continue"
    btn_cont = find_node_by_attrib(xml_path, "text", "Continue")
    if btn_cont is None:
        xml_path = dump_xml(profile_name, "02_pin_setup_after_typing")
        btn_cont = find_node_by_attrib(xml_path, "text", "Continue")
        
    if btn_cont is not None:
        log("Tapping Continue for first PIN...")
        tap_node(btn_cont)
    else:
        log("[CRITICAL] 'Continue' button not found after entering first PIN!")
        return
        
    # --- SCREEN 2b: PIN Setup Step (Confirm PIN) ---
    log("--- Screening PIN Setup Step (Confirm PIN) ---")
    time.sleep(1)
    capture_screenshot(profile_name, "02b_pin_confirm", "before_scroll")
    xml_path = dump_xml(profile_name, "02b_pin_confirm")
    
    key1 = find_node_by_attrib(xml_path, "text", "1")
    key2 = find_node_by_attrib(xml_path, "text", "2")
    key3 = find_node_by_attrib(xml_path, "text", "3")
    key4 = find_node_by_attrib(xml_path, "text", "4")
    
    if key1 is not None and key2 is not None and key3 is not None and key4 is not None:
        log("Tapping digits 1, 2, 3, 4 on keypad for confirmation...")
        tap_node(key1)
        tap_node(key2)
        tap_node(key3)
        tap_node(key4)
    else:
        log("[CRITICAL] Keypad digits not found in PIN confirm!")
        return
        
    btn_cont = find_node_by_attrib(xml_path, "text", "Continue")
    if btn_cont is None:
        xml_path = dump_xml(profile_name, "02b_pin_confirm_after_typing")
        btn_cont = find_node_by_attrib(xml_path, "text", "Continue")
        
    if btn_cont is not None:
        log("Tapping Continue to save PIN...")
        tap_node(btn_cont)
    else:
        log("[CRITICAL] 'Continue' button not found after entering confirmation PIN!")
        return
        
    # --- SCREEN 3: Contact Shortcuts Setup ---
    log("--- Screening Contact Shortcuts Setup ---")
    time.sleep(1)
    capture_screenshot(profile_name, "03_contact_setup", "before_scroll")
    xml_path = dump_xml(profile_name, "03_contact_setup")
    
    swipe_scroll(active_size)
    capture_screenshot(profile_name, "03_contact_setup", "after_scroll")
    
    # Enter Parent Name & Phone
    edit_texts = find_editText_nodes(xml_path)
    if len(edit_texts) >= 2:
        log("Entering Parent Contact details...")
        # Name
        tap_node(edit_texts[0])
        type_text("Mom")
        # Phone
        tap_node(edit_texts[1])
        type_text("1234567890")
        
        # Hide keyboard if needed (e.g. tapping outside or pressing back)
        run_cmd(["adb", "shell", "input", "keyevent", "4"]) # BACK to close keyboard
        time.sleep(0.5)
    else:
        log("[CRITICAL] Parent Contact EditText fields not found!")
        return
        
    # Re-dump XML after scroll/typing to find Save & Next button
    xml_path = dump_xml(profile_name, "03_contact_setup_typed")
    btn_save = find_node_by_attrib(xml_path, "text", "Save & Next")
    if btn_save is None:
        # Try to scroll down to find it
        swipe_scroll(active_size)
        xml_path = dump_xml(profile_name, "03_contact_setup_scrolled")
        btn_save = find_node_by_attrib(xml_path, "text", "Save & Next")
        
    if btn_save is not None:
        log("Tapping 'Save & Next'...")
        tap_node(btn_save)
    else:
        log("[CRITICAL] 'Save & Next' button completely unreachable in Contact Setup!")
        return
        
    # --- SCREEN 4: App Selection Step ---
    log("--- Screening App Selection Step ---")
    time.sleep(1)
    capture_screenshot(profile_name, "04_app_selection", "before_scroll")
    xml_path = dump_xml(profile_name, "04_app_selection")
    
    swipe_scroll(active_size)
    capture_screenshot(profile_name, "04_app_selection", "after_scroll")
    
    # Checkbox select the first app
    # Checkboxes are android.widget.CheckBox
    checkbox = find_node_by_attrib(xml_path, "class", "android.widget.CheckBox")
    if checkbox is None:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        # Find first clickable node that might be a row
        for node in root.iter('node'):
            if node.attrib.get('clickable') == "true" and "Approve" not in node.attrib.get('text') and node.attrib.get('text') != "":
                checkbox = node
                break
                
    if checkbox is not None:
        log("Selecting an app...")
        tap_node(checkbox)
    else:
        log("[WARNING] No apps found to select in the list.")
        
    # Find and tap Continue button
    btn_cont = find_node_by_attrib(xml_path, "text", "Continue")
    if btn_cont is None:
        xml_path = dump_xml(profile_name, "04_app_selection_scrolled")
        btn_cont = find_node_by_attrib(xml_path, "text", "Continue")
        
    if btn_cont is not None:
        log("Tapping Continue...")
        tap_node(btn_cont)
    else:
        log("[CRITICAL] 'Continue' button completely unreachable in App Selection!")
        return
        
    # --- SCREEN 5: Completion Step ---
    log("--- Screening Completion Step ---")
    time.sleep(1)
    capture_screenshot(profile_name, "05_completion", "before_scroll")
    xml_path = dump_xml(profile_name, "05_completion")
    
    swipe_scroll(active_size)
    capture_screenshot(profile_name, "05_completion", "after_scroll")
    
    # Find and tap Finish & Launch button
    btn_finish = find_node_by_attrib(xml_path, "text", "Finish & Launch")
    if btn_finish is None:
        xml_path = dump_xml(profile_name, "05_completion_scrolled")
        btn_finish = find_node_by_attrib(xml_path, "text", "Finish & Launch")
        
    if btn_finish is not None:
        log("Tapping 'Finish & Launch'...")
        tap_node(btn_finish)
    else:
        log("[CRITICAL] 'Finish & Launch' button completely unreachable in Completion screen!")
        return
        
    # --- CHILD HOME SCREEN (Verify landing) ---
    log("--- Screening Child Home Screen (Verification) ---")
    time.sleep(2)
    capture_screenshot(profile_name, "06_child_home", "landing")
    xml_path = dump_xml(profile_name, "06_child_home")
    
    # Check if we landed on Child Home screen (verify presence of Lock button or Date/Time text or Mode switcher)
    is_home = find_node_by_attrib(xml_path, "content-desc", "Parent Dashboard") is not None
    if is_home:
        log("Success: Landed on Child Home Screen!")
    else:
        log("[WARNING] Could not confirm Landing on Child Home Screen. XML check failed.")
        
    log(f"==================== PROFILE {profile_name} COMPLETED ====================\n")

def main():
    log("Reading default device values...")
    defaults = get_device_info()
    log(f"Defaults: {defaults}")
    
    try:
        # Run Profile A
        run_audit_for_profile("profileA", "412x915", 420, 1.0, "gestural")
        
        # Run Profile B
        run_audit_for_profile("profileB", "360x640", 420, 1.0, "gestural")
        
        # Run Profile C
        run_audit_for_profile("profileC", "360x640", 420, 1.3, "gestural")
        
        # Run Profile D
        run_audit_for_profile("profileD", "412x915", 420, 1.3, "gestural")
        
        # Run Profile E
        run_audit_for_profile("profileE", defaults["size"], defaults["density"], 1.0, "threebutton")
        
    finally:
        # Restore settings
        restore_device(defaults)
        log_file.close()

if __name__ == "__main__":
    main()
