# Child Home Screen Concept

## Screen purpose

Give the child a predictable, safe, and useful phone surface.

## Required elements

- active mode label
- large approved app tiles
- parent call tile
- emergency tile
- simple date/time
- optional camera tile if allowed

## Tile behavior

Each tile should:

- show icon
- show clear label
- have large tap target
- open the selected app or action
- avoid hidden long-press behavior in child mode

## MVP layout

Recommended first layout:

- top: mode label + time
- first row: parent call + emergency
- remaining grid: approved apps for current mode

## Mode behavior

### Home Mode

Shows:

- learning apps
- approved games
- communication apps if allowed
- camera/photos if allowed
- parent/emergency tiles

### School Mode

Shows:

- learning apps
- parent call
- emergency
- calculator/notes if allowed

Hides:

- games
- entertainment
- social apps
- browser unless allowed

### Sleep Mode

Shows:

- parent call
- emergency
- optional clock

Hides:

- most apps
- games
- entertainment

## Empty state

If no app is assigned to a mode, show a safe message:

> No apps have been added to this mode yet. Ask your parent to add apps.

Do not show a full app drawer as a fallback.

## Long-press behavior

Long press should not expose edit controls in child mode unless parent PIN is entered.
