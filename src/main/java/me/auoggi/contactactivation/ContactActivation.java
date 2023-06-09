package me.auoggi.contactactivation;

import me.auoggi.contactactivation.block.ModBlocks;
import me.auoggi.contactactivation.block.custom.ContactButton;
import me.auoggi.contactactivation.block.custom.ContactLever;
import me.auoggi.contactactivation.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ContactActivation.MOD_ID)
public class ContactActivation {

    public static final String MOD_ID = "contactactivation";

    public ContactActivation() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(eventBus);
        ModBlocks.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {
        if (event instanceof PlayerInteractEvent.RightClickBlock) {
            if(!event.getWorld().isClientSide() && !(event.getItemStack().getItem() instanceof BlockItem)) {
                for (Direction direction : Direction.values()) {
                    BlockPos pos = event.getPos().relative(direction);
                    BlockState state = event.getWorld().getBlockState(pos);
                    if (state.is(ModBlocks.CONTACT_BUTTON.get()) && state.getValue(ContactButton.FACING).getOpposite() == direction) {
                        ((ContactButton) state.getBlock()).activate(event.getWorld(), pos, event.getPlayer());
                    } else if (state.is(ModBlocks.CONTACT_LEVER.get()) && state.getValue(ContactLever.FACING).getOpposite() == direction) {
                        ((ContactLever) state.getBlock()).activate(event.getWorld(), pos, event.getPlayer());
                    }
                }
            }
        }
    }
}
