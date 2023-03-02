package fr.frostbreker.onetwenty.objects.blocks.custom.chiseledbookshelf;

import com.mojang.logging.LogUtils;
import fr.frostbreker.onetwenty.objects.blocks.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.function.Predicate;

public class ChiseledBookShelfBlockEntity2 extends BlockEntity implements Container {
    public static final int MAX_BOOKS_IN_STORAGE = 6;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int lastInteractedSlot = -1;

    public ChiseledBookShelfBlockEntity2(BlockPos p_249541_, BlockState p_251752_) {
        super(ModBlockEntities.CHISELED_BOOKSHELF.get(), p_249541_, p_251752_);
    }

    private void updateState(int p_261806_) {
        if (p_261806_ >= 0 && p_261806_ < 6) {
            this.lastInteractedSlot = p_261806_;
            BlockState blockstate = this.getBlockState();

            for(int i = 0; i < ChiseledBookShelfBlock2.SLOT_OCCUPIED_PROPERTIES.size(); ++i) {
                boolean flag = !this.getItem(i).isEmpty();
                BooleanProperty booleanproperty = ChiseledBookShelfBlock2.SLOT_OCCUPIED_PROPERTIES.get(i);
                blockstate = blockstate.setValue(booleanproperty, Boolean.valueOf(flag));
            }

            Objects.requireNonNull(this.level).setBlock(this.worldPosition, blockstate, 3);
        } else {
            LOGGER.error("Expected slot 0-5, got {}", (int)p_261806_);
        }
    }

    public void load(CompoundTag p_249911_) {
        this.items.clear();
        ContainerHelper.loadAllItems(p_249911_, this.items);
        this.lastInteractedSlot = p_249911_.getInt("last_interacted_slot");
    }

    protected void saveAdditional(CompoundTag p_251872_) {
        ContainerHelper.saveAllItems(p_251872_, this.items, true);
        p_251872_.putInt("last_interacted_slot", this.lastInteractedSlot);
    }

    public int count() {
        return (int)this.items.stream().filter(Predicate.not(ItemStack::isEmpty)).count();
    }

    public void clearContent() {
        this.items.clear();
    }

    public int getContainerSize() {
        return 6;
    }

    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    public ItemStack getItem(int p_256203_) {
        return this.items.get(p_256203_);
    }

    public ItemStack removeItem(int p_255828_, int p_255673_) {
        ItemStack itemstack = Objects.requireNonNullElse(this.items.get(p_255828_), ItemStack.EMPTY);
        this.items.set(p_255828_, ItemStack.EMPTY);
        if (!itemstack.isEmpty()) {
            this.updateState(p_255828_);
        }

        return itemstack;
    }

    public ItemStack removeItemNoUpdate(int p_255874_) {
        return this.removeItem(p_255874_, 1);
    }

    public void setItem(int p_256610_, ItemStack itemStack) {
        if (itemStack.is(ItemTags.LECTERN_BOOKS) || itemStack.is(Items.BOOK) || itemStack.is(Items.ENCHANTED_BOOK)) {
            this.items.set(p_256610_, itemStack);
            this.updateState(p_256610_);
        }

    }

    public int getMaxStackSize() {
        return 1;
    }

    public boolean stillValid(Player p_256481_) {
        if (this.level == null) {
            return false;
        } else if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return !(p_256481_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
        }
    }

    public boolean canPlaceItem(int p_256567_, ItemStack p_255922_) {
        return p_255922_.is(ItemTags.LECTERN_BOOKS) || p_255922_.is(Items.BOOK) || p_255922_.is(Items.ENCHANTED_BOOK) && this.getItem(p_256567_).isEmpty();
    }

    public int getLastInteractedSlot() {
        return this.lastInteractedSlot;
    }
}