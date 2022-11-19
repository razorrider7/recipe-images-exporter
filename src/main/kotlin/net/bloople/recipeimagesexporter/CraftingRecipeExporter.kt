package net.bloople.recipeimagesexporter;

import net.bloople.recipeimagesexporter.RecipeImagesExporterMod.LOGGER
import net.minecraft.item.ItemStack
import java.awt.image.BufferedImage
import java.lang.Integer.max
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO


class CraftingRecipeExporter(
    private val recipeInfo: CraftingRecipeInfo,
    private val exportDir: Path,
    private val itemsData: ItemsData
) : Exporter {
    override fun export() {
        LOGGER.info("exporting ${recipeInfo.recipePath}")
        loadImages()

        val recipeFilePath = exportDir.resolve("${recipeInfo.recipePath}.png")
        Files.createDirectories(recipeFilePath.parent)

        val cropWidth = max(
            214 + rightImage.width,
            recipeInfo.items.maxOf { itemsData.itemNameWidths[it]!! } + 44 + 8)
        val cropHeight = 122 + bottomImage.height + 6 + (recipeInfo.items.size * 30) +
            (max(recipeInfo.items.size - 1, 0) * 4)

        val outputImage = BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_ARGB).apply {
            raster.setRect(0, 0, baseImage.getData(0, 0, width, height))
            raster.setRect(0, height - 2, bottomImage.getData(0, 0, width, 2))
            raster.setRect(width - 2, 0, rightImage.getData(0, 0, 2, height))

            createGraphics().use {
                if(recipeInfo.slot1 != null) drawImage(itemsData.slotImage(recipeInfo.slot1), 11, 11, null)
                if(recipeInfo.slot2 != null) drawImage(itemsData.slotImage(recipeInfo.slot2), 47, 11, null)
                if(recipeInfo.slot3 != null) drawImage(itemsData.slotImage(recipeInfo.slot3), 83, 11, null)
                if(recipeInfo.slot4 != null) drawImage(itemsData.slotImage(recipeInfo.slot4), 11, 47, null)
                if(recipeInfo.slot5 != null) drawImage(itemsData.slotImage(recipeInfo.slot5), 47, 47, null)
                if(recipeInfo.slot6 != null) drawImage(itemsData.slotImage(recipeInfo.slot6), 83, 47, null)
                if(recipeInfo.slot7 != null) drawImage(itemsData.slotImage(recipeInfo.slot7), 11, 83, null)
                if(recipeInfo.slot8 != null) drawImage(itemsData.slotImage(recipeInfo.slot8), 47, 83, null)
                if(recipeInfo.slot9 != null) drawImage(itemsData.slotImage(recipeInfo.slot9), 83, 83, null)
                drawImage(itemsData.outputImage(recipeInfo.output), 166, 46, null)

                val x = 8
                var y = 122

                for(item in recipeInfo.items) {
                    drawImage(itemsData.slotImage(ItemStack(item, 1)), x, y, null)
//                    val labelImage = recipesExporter.getLabel(item)
//                    raster.setRect(44, y + 7, labelImage.getData(0, 0, width - 44 - 2, labelImage.height))
                    drawImage(itemsData.labelImage(item), 44, y + 6, null)
                    y += 34
                }
            }
        }

        ImageIO.write(outputImage, "PNG", recipeFilePath.toFile())
    }

    companion object {
        private const val craftingTopLeftImage = "iVBORw0KGgoAAAANSUhEUgAAAgAAAAIABAMAAAAGVsnJAAAAFVBMVEXb29ubm5tbW1vGxsY3NzeLi4v////TAbtBAAACpklEQVR42u3auQ3DMBAEQPXkFtTCtsD+S3BOQwYDAba4s+GFA5DH545D2vPqDgAAAAAAOOdkpQJgI4BMGSsVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAM0A51UCoAQgFxkAAAD4X4CMOSsVABstgdtGZADYBAFUAgRAO0AAtAMEQDtAALQDBEAfwFQB0A4QAJYAAG0QgKMwANdhAJ7EAHgWB+BrDAAAAEZkABiRAWATBAAAQA/AZ7P81jQB7AfwvAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHQB3DciA+CZAM0BAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA8LO8AfPNCN6dp75vAAAAAElFTkSuQmCC"
        private const val craftingRightImage = "iVBORw0KGgoAAAANSUhEUgAAAAIAAAIABAMAAABaz6A1AAAAFVBMVEXb29ubm5tbW1vGxsY3NzeLi4v////TAbtBAAAAFUlEQVQoz2NgZBBiUBqFo3AUjkQIAEg6Q9CIZupOAAAAAElFTkSuQmCC"
        private const val craftingBottomImage = "iVBORw0KGgoAAAANSUhEUgAAAgAAAAACBAMAAADcAXnrAAAAFVBMVEXb29ubm5tbW1vGxsY3NzeLi4v////TAbtBAAAAEklEQVQoz2NgVBrZgEFohAcAAPfOQ9CrWDAMAAAAAElFTkSuQmCC"
        private lateinit var baseImage: BufferedImage
        private lateinit var rightImage: BufferedImage
        private lateinit var bottomImage: BufferedImage

        private fun loadImages() {
            if(!::baseImage.isInitialized) baseImage = ImageIO.read(decodeBase64(craftingTopLeftImage)).asARGB()
            if(!::rightImage.isInitialized) rightImage = ImageIO.read(decodeBase64(craftingRightImage)).asARGB()
            if(!::bottomImage.isInitialized) bottomImage = ImageIO.read(decodeBase64(craftingBottomImage)).asARGB()
        }
    }
}