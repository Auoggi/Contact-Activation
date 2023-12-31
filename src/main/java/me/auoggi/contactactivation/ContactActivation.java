package me.auoggi.contactactivation;

import me.auoggi.contactactivation.block.ContactButton;
import me.auoggi.contactactivation.block.ContactLever;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod(ContactActivation.MOD_ID)
public class ContactActivation {

    public static final String MOD_ID = "contactactivation";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ContactActivation.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ContactActivation.MOD_ID);

    public static final RegistryObject<Block> CONTACT_BUTTON = registerBlock("contact_button",
            () -> new ContactButton(BlockBehaviour.Properties.of(Material.METAL).strength(5f).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> CONTACT_LEVER = registerBlock("contact_lever",
            () -> new ContactLever(BlockBehaviour.Properties.of(Material.METAL).strength(5f).requiresCorrectToolForDrops()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        ITEMS.register(name, () -> new BlockItem(toReturn.get(), new Item.Properties().tab(CreativeModeTab.TAB_REDSTONE)));
        return toReturn;
    }

    public ContactActivation() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(eventBus);
        BLOCKS.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {
        if (event instanceof PlayerInteractEvent.RightClickBlock) {
            if(!event.getWorld().isClientSide() && !(event.getItemStack().getItem() instanceof BlockItem)) {
                for (Direction direction : Direction.values()) {
                    BlockPos pos = event.getPos().relative(direction);
                    BlockState state = event.getWorld().getBlockState(pos);
                    if (state.is(CONTACT_BUTTON.get()) && state.getValue(ContactButton.FACING).getOpposite() == direction) {
                        ((ContactButton) state.getBlock()).activate(event.getWorld(), pos, event.getPlayer());
                    } else if (state.is(CONTACT_LEVER.get()) && state.getValue(ContactLever.FACING).getOpposite() == direction) {
                        ((ContactLever) state.getBlock()).activate(event.getWorld(), pos, event.getPlayer());
                    }
                }
            }
        }
    }
}
