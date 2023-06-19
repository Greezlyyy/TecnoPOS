package me.wiceh.tecnopos.listeners;

import me.wiceh.tecnopos.TecnoPOS;
import net.milkbowl.vault.economy.Economy;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class onInteract implements Listener {

    private boolean isPrice(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }catch(NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getItemMeta().getDisplayName().equals("§fPOS")) {
                String azienda = event.getItem().getItemMeta().getLore().toString().replace("§eAzienda: ", "").replace("[", "").replace("]", "");
                openGUI1(event.getPlayer(), azienda);
            }
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if(event.getView().getTitle().startsWith("§8POS: §c")) {
                event.setCancelled(true);
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§6Stampa scontrino")) {
                    String azienda = player.getInventory().getItemInMainHand().getItemMeta().getLore().toString().replace("§eAzienda: ", "").replace("[", "").replace("]", "");
                    openGUI2(player, azienda);
                }else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("§6Paga con carta")) {
                    player.sendMessage("§cComing soon...");
                }else if(event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                    String displayName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                    String azienda = player.getInventory().getItemInMainHand().getItemMeta().getLore().toString().replace("§eAzienda: ", "").replace("[", "").replace("]", "");
                    openGUI3(player, azienda, displayName);
                }
            }
        }
    }

    private void openGUI1(Player player, String azienda) {

        Inventory inventory = Bukkit.createInventory(null, 9, "§8POS: §c" + azienda);

        ItemStack pagaScontrino = new ItemStack(Material.PAPER);
        ItemMeta pagaScontrinoMeta = pagaScontrino.getItemMeta();
        pagaScontrinoMeta.setDisplayName("§6Stampa scontrino");
        pagaScontrinoMeta.setLore(Arrays.asList("§7Clicca qui per emettere uno scontrino"));
        pagaScontrino.setItemMeta(pagaScontrinoMeta);

        ItemStack pagaCarta = new ItemStack(Material.BOOK);
        ItemMeta pagaCartaMeta = pagaCarta.getItemMeta();
        pagaCartaMeta.setDisplayName("§6Paga con carta");
        pagaCartaMeta.setLore(Arrays.asList("§7Clicca qui per effettuare il pagamento con carta"));
        pagaCarta.setItemMeta(pagaCartaMeta);

        inventory.setItem(3, pagaScontrino);
        inventory.setItem(5, pagaCarta);

        player.openInventory(inventory);
    }

    private void openGUI2(Player player, String azienda) {

        Inventory inventory = Bukkit.createInventory(null, 9, "§8POS: §c" + azienda);

        List<Player> nearbyPlayers = getNearbyPlayers(player, 8);

        if (!nearbyPlayers.isEmpty()) {

            for(Player nearbyPlayer : nearbyPlayers) {
                ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();

                headMeta.setDisplayName("§e§o" + nearbyPlayer.getName());
                List<String> headLore = new ArrayList<>();
                headLore.add(" ");
                headLore.add("§6Stampa scontrino");
                headMeta.setLore(headLore);
                headMeta.setOwner(nearbyPlayer.getName());

                playerHead.setItemMeta(headMeta);

                inventory.addItem(playerHead);
            }

            player.openInventory(inventory);
        } else {
            player.sendMessage("§cNon ci sono giocatori nelle vicinanze!");
        }
    }

    private void openGUI3(Player player, String azienda, String displayName) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    if(stateSnapshot.getText() != null) {
                        if(isPrice(stateSnapshot.getText())) {
                            String prezzo = stateSnapshot.getText();
                            openGUI4(player, azienda, displayName, prezzo);
                            return Arrays.asList(AnvilGUI.ResponseAction.close());
                        }else {
                            return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Riprova!"));
                        }
                    } else {
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Prezzo"));
                    }
                })
                .text("Prezzo")
                .title("§eInserisci prezzo")
                .plugin(TecnoPOS.getInstance())
                .open(player);
    }

    private void openGUI4(Player player, String azienda, String displayName, String prezzo) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    if(stateSnapshot.getText() != null) {
                        String articoli = stateSnapshot.getText();
                        faiScontrino(player, azienda, displayName, prezzo, articoli);
                        return Arrays.asList(AnvilGUI.ResponseAction.close());
                    } else {
                        return Arrays.asList(AnvilGUI.ResponseAction.replaceInputText("Articoli"));
                    }
                })
                .text("Articoli")
                .title("§eInserisci articoli")
                .plugin(TecnoPOS.getInstance())
                .open(player);
    }

    private void faiScontrino(Player player, String azienda, String displayName, String prezzo, String articoli) {
        Economy economy = TecnoPOS.getEconomy();
        double balance = economy.getBalance(player);
        if(balance >= Integer.parseInt(prezzo) || balance >= Double.parseDouble(prezzo)) {
            ItemStack scontrino = new ItemStack(Material.PAPER, 2);
            ItemMeta scontrinoMeta = scontrino.getItemMeta();
            scontrinoMeta.setDisplayName("§aScontrino " + azienda);

            List<String> scontrinoLore = new ArrayList<>();

            scontrinoLore.add("§7Prezzo: §e" + prezzo + "€");
            scontrinoLore.add("§7Cassiere: §e" + player.getName());
            scontrinoLore.add("§7Articoli: §e" + articoli);
            scontrinoLore.add("§7Cliente: §e" + displayName);

            scontrinoMeta.setLore(scontrinoLore);

            scontrino.setItemMeta(scontrinoMeta);

            player.getInventory().addItem(scontrino);

            economy.withdrawPlayer(player, Integer.parseInt(prezzo));

            player.sendMessage("§aHai creato lo scontrino con successo!");
        }else {
            player.sendMessage("§cNon hai abbastanza soldi!");
        }
    }

    private List<Player> getNearbyPlayers(Player player, int radius) {
        List<Player> nearbyPlayers = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player && p.getLocation().distance(player.getLocation()) <= radius) {
                nearbyPlayers.add(p);
            }
        }

        return nearbyPlayers;
    }
}
