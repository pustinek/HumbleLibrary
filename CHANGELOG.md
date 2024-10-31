## v2.2.0
- ItemBuilder replaced `loreReplacements` with `replacements` to give support to replacements in the name as well.

## v2.1.2
- [FIX] ItemBuilder -> when cloning an item and setting lore (`ItemBuilder(item).setLore()`) on the same item, the lore was duplicated instead of being replaced