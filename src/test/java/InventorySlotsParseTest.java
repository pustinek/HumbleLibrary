import me.pustinek.humblelibrary.exceptions.ItemSlotParseException;
import me.pustinek.humblelibrary.utils.InventoryUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Inventory slots parse Tests \uD83E\uDDF0")
class InventorySlotsParseTest {

    @Test
    @DisplayName("default comma")
    void parseDefault() {
        String input = "1,2,3,4,5";
        List<Integer> expected = IntStream.of(1, 2, 3, 4, 5).boxed().collect(Collectors.toList());
        try {
            assertEquals(expected, InventoryUtils.parseSlots(input));
        } catch (ItemSlotParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("between [0-5]")
    void parseBetween() {
        String input = "0-5";
        List<Integer> expected = IntStream.of(0, 1, 2, 3, 4, 5).boxed().collect(Collectors.toList());
        try {
            assertEquals(expected, InventoryUtils.parseSlots(input));
        } catch (ItemSlotParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("mixed base")
    void parseMixedBase() {
        String input = "0-5,10-12";
        List<Integer> expected = IntStream.of(0, 1, 2, 3, 4, 5, 10, 11, 12).boxed().collect(Collectors.toList());
        try {
            assertEquals(expected, InventoryUtils.parseSlots(input));
        } catch (ItemSlotParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("relative slots")
    void parseRelative() {
        String input = "0|0,1,2";
        List<Integer> expected = IntStream.of(0, 1, 2).boxed().collect(Collectors.toList());
        try {
            assertEquals(expected, InventoryUtils.parseSlots(input));
        } catch (ItemSlotParseException e) {
            e.printStackTrace();
        }
    }

}
