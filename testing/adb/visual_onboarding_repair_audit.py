#!/usr/bin/env python3
import os
import sys
import time
import subprocess
import xml.etree.ElementTree as ET
import re

APP_PACKAGE = "com.easyui.guardianlauncher"
MAIN_ACTIVITY = "com.easyui.guardianlauncher.MainActivity"

# Output directories inside the Gemini workspace artifacts folder
BASE_ARTIFACT_DIR = "/home/munaim/.gemini/antigravity/brain/4f1b59bf-cd06-45bf-9df1-c110d0ab2c2c/artifacts/onboarding-layout-repair"
SCREENSHOT_DIR = os.path.join(BASE_ARTIFACT_DIR, "screenshots")
XML_DIR = os.path.join(BASE_ARTIFACT_DIR, "xml")
LOG_DIR = os.path.join(BASE_ARTIFACT_DIR, "logs")
REPORT_DIR = os.path.join(BASE_ARTIFACT_DIR, "reports")

# Ensure directories exist
os.makedirs(SCREENSHOT_DIR, exist_ok=True)
os.makedirs(XML_DIR, exist_ok=True)
os.makedirs(LOG_DIR, exist_ok=True)
os.makedirs(REPORT_DIR, exist_ok=True)

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
    original_font = font_out.strip() if font_out.strip() else "1.0"
    
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
        
    time.sleep(2) # Give layout time to adjust

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
        if not os.path.exists(xml_path):
            return None
        tree = ET.parse(xml_path)
        root = tree.getroot()
        for node in root.iter('node'):
            if node.attrib.get(attrib_name) == attrib_val:
                return node
        return None
    except Exception as e:
        log(f"Error parsing XML: {e}")
        return None

def find_node_by_id(xml_path, test_tag):
    full_id = f"{APP_PACKAGE}:id/{test_tag}"
    node = find_node_by_attrib(xml_path, "resource-id", full_id)
    if node is None:
        # Fall back to text matching as a secondary measure
        node = find_node_by_attrib(xml_path, "text", test_tag)
    return node

def find_editText_nodes(xml_path):
    try:
        if not os.path.exists(xml_path):
            return []
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

def swipe_scroll(size_str):
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
    adb_text = text.replace(" ", "%s")
    run_cmd(["adb", "shell", "input", "text", adb_text])
    time.sleep(0.5)

def run_audit_for_profile(profile_name, size, density, font_scale, navbar):
    log(f"\n==================== STARTING PROFILE {profile_name} ====================")
    apply_profile(profile_name, size, density, font_scale, navbar)
    
    _, wm_size_out, _ = run_cmd(["adb", "shell", "wm", "size"])
    log(f"Active wm size output: {wm_size_out}")
    active_size = "412x915"
    m = re.search(r'Override size: (\d+x\d+)', wm_size_out)
    if not m:
        m = re.search(r'Physical size: (\d+x\d+)', wm_size_out)
    if m:
        active_size = m.group(1)
    
    clean_and_launch()
    
    status = {
        "welcome": "FAIL",
        "pin_create": "FAIL",
        "pin_confirm": "FAIL",
        "contacts": "FAIL",
        "apps": "FAIL",
        "completion": "FAIL",
        "home": "FAIL"
    }

    # --- SCREEN 1: Welcome Step ---
    log("--- Screening Welcome Step ---")
    capture_screenshot(profile_name, "01_welcome", "before_scroll")
    xml_path = dump_xml(profile_name, "01_welcome")
    
    # Try finding button by ID (resource id testTag)
    btn = find_node_by_id(xml_path, "onboarding_primary_button")
    if btn is None:
        btn = find_node_by_attrib(xml_path, "text", "Get Started")
        
    if btn is not None:
        log("Found 'Get Started' button. Tapping...")
        status["welcome"] = "PASS"
        tap_node(btn)
    else:
        log("[WARNING] 'Get Started' button not found initially. Swiping...")
        swipe_scroll(active_size)
        capture_screenshot(profile_name, "01_welcome", "after_scroll")
        xml_path = dump_xml(profile_name, "01_welcome_scrolled")
        btn = find_node_by_id(xml_path, "onboarding_primary_button")
        if btn is None:
            btn = find_node_by_attrib(xml_path, "text", "Get Started")
        if btn is not None:
            status["welcome"] = "PASS"
            tap_node(btn)
        else:
            log("[CRITICAL] Welcome button completely unreachable.")
            return status

    # --- SCREEN 2: PIN Setup Step ---
    log("--- Screening PIN Setup Step (Create PIN) ---")
    time.sleep(1.5)
    capture_screenshot(profile_name, "02_pin_setup", "before_scroll")
    xml_path = dump_xml(profile_name, "02_pin_setup")
    
    # Check if keypad keys are present
    k1 = find_node_by_id(xml_path, "pin_digit_1")
    k2 = find_node_by_id(xml_path, "pin_digit_2")
    k3 = find_node_by_id(xml_path, "pin_digit_3")
    k4 = find_node_by_id(xml_path, "pin_digit_4")
    
    # Fallback to text matching if ID not found
    if k1 is None: k1 = find_node_by_attrib(xml_path, "text", "1")
    if k2 is None: k2 = find_node_by_attrib(xml_path, "text", "2")
    if k3 is None: k3 = find_node_by_attrib(xml_path, "text", "3")
    if k4 is None: k4 = find_node_by_attrib(xml_path, "text", "4")

    if k1 is not None and k2 is not None and k3 is not None and k4 is not None:
        log("Tapping digits 1, 2, 3, 4 on keypad...")
        tap_node(k1)
        tap_node(k2)
        tap_node(k3)
        tap_node(k4)
        
        # Tap Continue button
        time.sleep(0.5)
        xml_path = dump_xml(profile_name, "02_pin_setup_typed")
        btn_cont = find_node_by_id(xml_path, "onboarding_primary_button")
        if btn_cont is None:
            btn_cont = find_node_by_attrib(xml_path, "text", "Continue")
            
        if btn_cont is not None:
            status["pin_create"] = "PASS"
            tap_node(btn_cont)
        else:
            log("[CRITICAL] 'Continue' button not found after entering first PIN!")
            return status
    else:
        log("[CRITICAL] Keypad digits not found in PIN setup!")
        return status

    # --- SCREEN 2b: PIN Setup Step (Confirm PIN) ---
    log("--- Screening PIN Setup Step (Confirm PIN) ---")
    time.sleep(1.5)
    capture_screenshot(profile_name, "02b_pin_confirm", "before_scroll")
    xml_path = dump_xml(profile_name, "02b_pin_confirm")
    
    k1 = find_node_by_id(xml_path, "pin_digit_1")
    k2 = find_node_by_id(xml_path, "pin_digit_2")
    k3 = find_node_by_id(xml_path, "pin_digit_3")
    k4 = find_node_by_id(xml_path, "pin_digit_4")
    
    if k1 is None: k1 = find_node_by_attrib(xml_path, "text", "1")
    if k2 is None: k2 = find_node_by_attrib(xml_path, "text", "2")
    if k3 is None: k3 = find_node_by_attrib(xml_path, "text", "3")
    if k4 is None: k4 = find_node_by_attrib(xml_path, "text", "4")

    if k1 is not None and k2 is not None and k3 is not None and k4 is not None:
        log("Tapping digits 1, 2, 3, 4 on keypad for confirmation...")
        tap_node(k1)
        tap_node(k2)
        tap_node(k3)
        tap_node(k4)
        
        time.sleep(0.5)
        xml_path = dump_xml(profile_name, "02b_pin_confirm_typed")
        btn_cont = find_node_by_id(xml_path, "onboarding_primary_button")
        if btn_cont is None:
            btn_cont = find_node_by_attrib(xml_path, "text", "Continue")
            
        if btn_cont is not None:
            status["pin_confirm"] = "PASS"
            tap_node(btn_cont)
        else:
            log("[CRITICAL] 'Continue' button not found after entering confirmation PIN!")
            return status
    else:
        log("[CRITICAL] Keypad digits not found in PIN confirm!")
        return status

    # --- SCREEN 3: Contact Shortcuts Setup ---
    log("--- Screening Contact Shortcuts Setup ---")
    time.sleep(1.5)
    capture_screenshot(profile_name, "03_contact_setup", "before_scroll")
    xml_path = dump_xml(profile_name, "03_contact_setup")
    
    # Enter Parent Name & Phone
    pname_node = find_node_by_id(xml_path, "parent_name_input")
    pphone_node = find_node_by_id(xml_path, "parent_phone_input")
    
    if pname_node is not None and pphone_node is not None:
        log("Entering Parent Contact details using IDs...")
        tap_node(pname_node)
        type_text("Mom")
        tap_node(pphone_node)
        type_text("1234567890")
    else:
        edit_texts = find_editText_nodes(xml_path)
        if len(edit_texts) >= 2:
            log("Entering Parent Contact details using EditText class fallback...")
            tap_node(edit_texts[0])
            type_text("Mom")
            tap_node(edit_texts[1])
            type_text("1234567890")
        else:
            log("[CRITICAL] Parent Contact inputs not found!")
            return status

    # Dismiss keyboard
    run_cmd(["adb", "shell", "input", "keyevent", "4"])
    time.sleep(0.5)
    
    # Tap Save & Next button
    xml_path = dump_xml(profile_name, "03_contact_setup_typed")
    btn_save = find_node_by_id(xml_path, "onboarding_primary_button")
    if btn_save is None:
        btn_save = find_node_by_attrib(xml_path, "text", "Save & Next")
        
    if btn_save is not None:
        status["contacts"] = "PASS"
        tap_node(btn_save)
    else:
        log("[CRITICAL] 'Save & Next' button not found in Contact Setup!")
        return status

    # --- SCREEN 4: App Selection Step ---
    log("--- Screening App Selection Step ---")
    time.sleep(1.5)
    capture_screenshot(profile_name, "04_app_selection", "before_scroll")
    xml_path = dump_xml(profile_name, "04_app_selection")
    
    # Look for checkbox by ID or fallback
    chk = find_node_by_id(xml_path, "app_selection_checkbox")
    if chk is None:
        chk = find_node_by_attrib(xml_path, "class", "android.widget.CheckBox")
        
    if chk is not None:
        log("Tapping checkbox...")
        tap_node(chk)
    else:
        log("[WARNING] No checkbox found to select.")
        
    # Tap Continue button
    btn_cont = find_node_by_id(xml_path, "onboarding_primary_button")
    if btn_cont is None:
        btn_cont = find_node_by_attrib(xml_path, "text", "Continue")
        
    if btn_cont is not None:
        status["apps"] = "PASS"
        tap_node(btn_cont)
    else:
        log("[CRITICAL] 'Continue' button not found in App Selection!")
        return status

    # --- SCREEN 5: Completion Step ---
    log("--- Screening Completion Step ---")
    time.sleep(1.5)
    capture_screenshot(profile_name, "05_completion", "before_scroll")
    xml_path = dump_xml(profile_name, "05_completion")
    
    # Tap Finish & Launch
    btn_finish = find_node_by_id(xml_path, "onboarding_primary_button")
    if btn_finish is None:
        btn_finish = find_node_by_attrib(xml_path, "text", "Finish & Launch")
        
    if btn_finish is not None:
        status["completion"] = "PASS"
        tap_node(btn_finish)
    else:
        log("[CRITICAL] 'Finish & Launch' button not found in Completion screen!")
        return status

    # --- CHILD HOME SCREEN ---
    log("--- Screening Child Home Screen (Verification) ---")
    time.sleep(3.0)
    capture_screenshot(profile_name, "06_child_home", "landing")
    xml_path = dump_xml(profile_name, "06_child_home")
    
    # Verify child home screen landing via content description or resource IDs
    is_home = (find_node_by_attrib(xml_path, "content-desc", "Parent Dashboard") is not None or
               find_node_by_attrib(xml_path, "text", "Parent Dashboard") is not None)
    if is_home:
        log("Success: Confirmed Landing on Child Home Screen!")
        status["home"] = "PASS"
    else:
        log("[WARNING] Landing check failed. Parent Dashboard element not found.")
        
    log(f"==================== PROFILE {profile_name} COMPLETED ====================\n")
    return status

def main():
    log("Starting Onboarding Layout Repair Visual Audit...")
    defaults = get_device_info()
    log(f"Device original properties: {defaults}")
    
    results = {}
    
    try:
        # Profile A — realistic medium phone
        results["A"] = run_audit_for_profile("profileA", "1082x2402", 420, 1.0, "gestural")
        
        # Profile B — realistic small phone
        results["B"] = run_audit_for_profile("profileB", "945x1680", 420, 1.0, "gestural")
        
        # Profile C — realistic small phone + large font
        results["C"] = run_audit_for_profile("profileC", "945x1680", 420, 1.3, "gestural")
        
        # Profile D — realistic medium phone + large font
        results["D"] = run_audit_for_profile("profileD", "1082x2402", 420, 1.3, "gestural")
        
        # Profile E — real/default 3-button navigation
        results["E"] = run_audit_for_profile("profileE", defaults["size"], defaults["density"], 1.0, "threebutton")
        
        # Profile F — ultra-compact stress test (Optional)
        results["F"] = run_audit_for_profile("profileF", "412x915", 420, 1.0, "gestural")

    except Exception as e:
        log(f"Audit execution crash: {e}")
    finally:
        restore_device(defaults)
        
    log("\n==================== AUDIT EXECUTION SUMMARY ====================")
    for p, state in results.items():
        log(f"Profile {p}: {state}")
        
    # Write the report matrix file
    matrix_path = os.path.join(REPORT_DIR, "ONBOARDING_SCREEN_MATRIX_AFTER_REPAIR.md")
    with open(matrix_path, "w") as mf:
        mf.write("# Onboarding Screen Visual Audit Matrix\n\n")
        mf.write("This matrix details the pass/fail status of all setup screens across all tested device profiles.\n\n")
        mf.write("| Screen | Profile A (Med/Gest) | Profile B (Small/Gest) | Profile C (Small/LargeFont) | Profile D (Med/LargeFont) | Profile E (3-Button) | Profile F (Stress) | Final Status |\n")
        mf.write("|---|---|---|---|---|---|---|---|\n")
        
        screens = [
            ("Welcome", "welcome"),
            ("PIN Create", "pin_create"),
            ("PIN Confirm", "pin_confirm"),
            ("Contact Shortcuts", "contacts"),
            ("App Selection", "apps"),
            ("Completion", "completion"),
            ("Child Home Landing", "home")
        ]
        
        for name, key in screens:
            row = f"| {name} "
            for p in ["A", "B", "C", "D", "E", "F"]:
                status = results.get(p, {}).get(key, "FAIL")
                color = "🟢 PASS" if status == "PASS" else "🔴 FAIL"
                row += f"| {color} "
            overall = "🟢 PASS" if all(results.get(p, {}).get(key, "FAIL") == "PASS" for p in ["A", "B", "C", "D", "E"]) else "🔴 FAIL/PARTIAL"
            row += f"| {overall} |\n"
            mf.write(row)
            
    log(f"Written screen matrix report to: {matrix_path}")
    
    # Write the report details file
    audit_report_path = os.path.join(REPORT_DIR, "ONBOARDING_VISUAL_AUDIT_AFTER_REPAIR.md")
    with open(audit_report_path, "w") as rf:
        rf.write("# Onboarding Visual Audit After Repair\n\n")
        rf.write("Visual layout verification of EasyUI Guardian Launcher Setup Wizard.\n\n")
        rf.write("## Execution Logs\n")
        rf.write("All profiles A through F were executed. Detailed step execution results:\n\n")
        for p in ["A", "B", "C", "D", "E", "F"]:
            rf.write(f"### Profile {p}\n")
            p_res = results.get(p, {})
            for scr, status in p_res.items():
                rf.write(f"- **{scr}**: {'PASS 🟢' if status == 'PASS' else 'FAIL 🔴'}\n")
            rf.write("\n")
            
    log(f"Written visual audit report to: {audit_report_path}")
    
    log_file.close()

if __name__ == "__main__":
    main()
