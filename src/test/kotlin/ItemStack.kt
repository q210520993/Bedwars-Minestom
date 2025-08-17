import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import org.junit.jupiter.api.Test

class ItemStack {
    @Test
    fun itemstackTag() {
        val tag = Tag.String("123")
        val itemStack = ItemStack.builder(Material.BLACK_STAINED_GLASS_PANE).set(tag, "test").build()
    }
}