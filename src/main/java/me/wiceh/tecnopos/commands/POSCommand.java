package me.wiceh.tecnopos.commands;

import me.wiceh.tecnopos.TecnoPOS;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class POSCommand implements CommandExecutor {

    private final TecnoPOS plugin;

    public POSCommand(TecnoPOS plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("pos.admin")) {
                if(args.length == 2) {
                    if(args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("ottieni")) {
                        String azienda = String.join(" ", args).replace("get ", "").replace("ottieni ", "");
                        givePOS(player, azienda);
                        player.sendMessage(plugin.prefix + "§aHai ottenuto un POS! §8(§e" + azienda + "§8)");
                    }else {
                        player.sendMessage("§6§lLista Comandi §7(/pos)\n§8▪ §b/pos ottieni <azienda>");
                    }
                }else {
                    player.sendMessage("§6§lLista Comandi §7(/pos)\n§8▪ §b/pos ottieni <azienda>");
                }
            }else {
                player.sendMessage("§cNon hai il permesso per eseguire questo comando!");
            }
        }else {
            System.out.println("§cQuesto comando è eseguibile solamente dai giocatori!");
        }
        return true;
    }

    private void givePOS(Player player, String azienda) {
        ItemStack pos = new ItemStack(Material.valueOf(plugin.getConfig().getString("pos-item")));
        ItemMeta posMeta = pos.getItemMeta();

        posMeta.setDisplayName("§fPOS");
        posMeta.setLore(Arrays.asList("§eAzienda: " + azienda));

        pos.setItemMeta(posMeta);

        player.getInventory().addItem(pos);
    }
}
