package Latch.Money4Mobs;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.PacketPlayOutChat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public abstract class MobKiller implements CommandExecutor {

    private static final List<MobModel> mm = MobConfigManager.getMobModelFromConfig();
    private static EntityDeathEvent ede;
    private static final Random rand = new Random();
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static double money = 0;
    private static final List<MobSpawnedReason> msr = new ArrayList<>();
    private static Boolean giveMoney = false;
    private static String language = "";
    private static Boolean showMessage = true;

    public static void rewardPlayerMoney(Player pa, Entity e, Economy econ) {
        setLanguage(pa);
        giveMoneyCheck(pa,e);
        if (Boolean.TRUE.equals(giveMoney)){
            setRange(e, pa);
            setDefaultDrops();
            setCustomDrops(e, pa);
            displayKillMessage(pa);
            sendKillMessage(pa, econ);
        }
    }

    public static void setEvent(EntityDeathEvent e) {
        ede = e;
    }

    public static void setLanguage(Player pa){
        int counter = 1;
        for(String users : UserManager.usersCfg.getConfigurationSection("users").getKeys(false)) {
            String userId = UserManager.usersCfg.getString("users.user-" + counter + ".userId");
            assert userId != null;
            if(userId.equalsIgnoreCase(pa.getUniqueId().toString())){
                language = UserManager.usersCfg.getString("users.user-" + counter + ".language");
            }
            counter++;
        }
    }

    public static void displayKillMessage(Player pa){
        int counter = 1;
        for(String users : UserManager.usersCfg.getConfigurationSection("users").getKeys(false)) {
            String userId = UserManager.usersCfg.getString("users.user-" + counter + ".userId");
            assert userId != null;
            if(userId.equalsIgnoreCase(pa.getUniqueId().toString())){
                showMessage = UserManager.usersCfg.getBoolean("users.user-" + counter + ".showMessage");
            }
            counter++;
        }
    }

    public static void sendActionBar(Player player, String message) {
        CraftPlayer p = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        p.getHandle().playerConnection.sendPacket(ppoc);
    }

    public static void sendKillMessage(Player pa, Economy econ){
        EconomyResponse r = econ.depositPlayer(pa, money);
        int counter = 1;
        for(String users : UserManager.usersCfg.getConfigurationSection("users").getKeys(false)) {
            String userId = UserManager.usersCfg.getString("users.user-" + counter + ".userId");
            assert userId != null;
            if(userId.equalsIgnoreCase(pa.getUniqueId().toString())){
                showMessage = UserManager.usersCfg.getBoolean("users.user-" + counter + ".showMessage");
            }
            boolean customMessage = true;
            customMessage = MobConfigManager.mobsCfg.getBoolean("customMessageOption.overrideKillMessage");
            if (pa.getUniqueId().toString().equals(userId)) {
                if (showMessage) {
                    if (r.amount != 0) {
                        if (r.transactionSuccess()) {
                            Double balance = r.balance;
                            df.format(balance);
                            df.setRoundingMode(RoundingMode.UP);
                            assert language != null;
                            if (Boolean.TRUE.equals(showMessage) && Boolean.TRUE.equals(customMessage)) {
                                String customMessageString = MobConfigManager.mobsCfg.getString("customMessageOption.customMessage");
                                List<String> customArray = Arrays.asList(customMessageString.split(" "));
                                List<String> colorArray = new ArrayList<>();
                                colorArray.add("DARK_RED");
                                colorArray.add("RED");
                                colorArray.add("GOLD");
                                colorArray.add("YELLOW");
                                colorArray.add("DARK_GREEN");
                                colorArray.add("GREEN");
                                colorArray.add("AQUA");
                                colorArray.add("DARK_AQUA");
                                colorArray.add("DARK_BLUE");
                                colorArray.add("BLUE");
                                colorArray.add("LIGHT_PURPLE");
                                colorArray.add("DARK_PURPLE");
                                colorArray.add("WHITE");
                                colorArray.add("GRAY");
                                colorArray.add("DARK_GRAY");
                                colorArray.add("BLACK");
                                ChatColor holder;
                                List<Object> object = new ArrayList<Object>();
                                boolean test = false;
                                for (String s: customArray){
                                    test = false;
                                    int count = StringUtils.countMatches(s, "%");
                                    if (count == 2){
                                        s = s.substring(s.indexOf("%") + 1);
                                        s = s.substring(0, s.indexOf("%"));
                                    }
                                    for (String color : colorArray){
                                        if (s.equals(color)){
                                            test = true;
                                            holder = ChatColor.valueOf(s);
                                            object.add(holder);
                                        }
                                    }
                                    if (s.equals("AMOUNT")){
                                        test = true;
                                        object.add(Math.round(r.amount));
                                    }
                                    if (s.equals("BALANCE")){
                                        test = true;
                                        object.add(df.format(balance));
                                    }
                                    if (s.equals("|")){
                                        test = true;
                                        object.add(" ");
                                    }
                                    if (Boolean.FALSE.equals(test)){
                                        object.add(s);
                                    }

                                }
                                String d = object.get(0).toString();
                                int count = 1;
                                for (Object wow: object){
                                    if (count > 1){
                                        d = new StringBuilder(d).append(wow).toString();
                                    }
                                    count++;
                                }
                                sendActionBar(pa, d);
                            }

                            if(Boolean.TRUE.equals(showMessage) && Boolean.FALSE.equals(customMessage)){
                                if (language.equalsIgnoreCase("French")){
                                    String message =  ChatColor.WHITE + "Vous avez reçu " + ChatColor.GREEN + Math.round(r.amount) + "$" +
                                            ChatColor.WHITE + " et vous avez maintenant " + ChatColor.GREEN + df.format(balance) + "$";
                                    sendActionBar(pa, message);
                                }
                                else if (language.equalsIgnoreCase("Spanish")){
                                    String message = ChatColor.WHITE + "Te dieron " + ChatColor.GREEN + "$" + Math.round(r.amount) +
                                            ChatColor.WHITE + " y ahora tienes " + ChatColor.GREEN + "$" + df.format(balance);
                                    sendActionBar(pa, message);
                                }
                                else if (language.equalsIgnoreCase("Chinese_Simplified")){
                                    String message = ChatColor.WHITE + "您获得了 " + ChatColor.GREEN + "$" + Math.round(r.amount) +
                                            ChatColor.WHITE + " 现在有 " + ChatColor.GREEN + "$" + df.format(balance);
                                    sendActionBar(pa, message);
                                }
                                else if (language.equalsIgnoreCase("Chinese_Traditional")){
                                    String message = ChatColor.WHITE + "你獲得 " + ChatColor.GREEN + "$" + Math.round(r.amount) +
                                            ChatColor.WHITE + " 身上金錢有 " + ChatColor.GREEN + "$" + df.format(balance);
                                    sendActionBar(pa, message);
                                }
                                else if (language.equalsIgnoreCase("Hindi")){
                                    String message = ChatColor.WHITE + "आपको " + ChatColor.GREEN + "$" + Math.round(r.amount) +
                                            ChatColor.WHITE + " दिया गया है और अब आपके पास " + ChatColor.GREEN + "$" + df.format(balance) + ChatColor.WHITE + " है।";
                                    sendActionBar(pa, message);
                                }
                                else if (language.equalsIgnoreCase("Italian")){
                                    String message = ChatColor.WHITE + "Hai guadagnato " + ChatColor.GREEN + "$" + Math.round(r.amount) +
                                            ChatColor.WHITE + " ed adesso hai " + ChatColor.GREEN + "$" + df.format(balance) + ChatColor.WHITE + ".";
                                    sendActionBar(pa, message);
                                }
                                else if (language.equalsIgnoreCase("German")){
                                    String message = ChatColor.WHITE + "Sie haben " + ChatColor.GREEN + "$" + Math.round(r.amount) +
                                            ChatColor.WHITE + " erhalten und haben jetzt " + ChatColor.GREEN + "$" + df.format(balance) + ChatColor.WHITE + ".";
                                    sendActionBar(pa, message);
                                }
                                else {
                                    String message = ChatColor.WHITE + "You were given " + ChatColor.GREEN + "$" + Math.round(r.amount) +
                                            ChatColor.WHITE + " and now have " + ChatColor.GREEN + "$" + df.format(balance);
                                    sendActionBar(pa, message);
                                }
                            }
                        }
                    }
                }
            }
            counter++;
        }

    }

    public static void setCustomDrops(Entity e, Player p){
        getLootingLevel();
        String es = e.toString();
        int randomNumber = rand.nextInt(100);

        MobConfigManager.mobsCfg.getBoolean("spawneggs");
        String[] name = es.split("Craft");

        for (MobModel mobModel : mm) {
            if (Boolean.TRUE.equals(mobModel.getCustomDrops())) {
                if (mobModel.getMobName().contains(name[1])) {
                    for (int j = 0; j < mobModel.getItems().size(); j++) {
                        int chance;
                        if (mobModel.getItems().get(j).getChance() == 0) {
                            chance = 100;
                        } else {
                            chance = mobModel.getItems().get(j).getChance();
                        }
                        if (randomNumber <= chance) {
                            String itemName = mobModel.getItems().get(j).getItemName();
                            Material m = Material.valueOf(itemName);
                            Integer amount = mobModel.getItems().get(j).getAmount();
                            ede.getDrops().add(new ItemStack(m, amount));
                        }
                    }
                }
            }

        }
    }

    public static void setDefaultDrops() {
        for (MobModel mobModel : mm) {
            if (ede.getEntity().getName().equalsIgnoreCase(mobModel.mobName)) {
                if (!Boolean.TRUE.equals(mobModel.getKeepDefaultDrops())) {
                    ede.getDrops().clear();
                }
            }
        }
    }

    public static void getSpawnReason(CreatureSpawnEvent e) {
        msr.add(new MobSpawnedReason(e.getSpawnReason().toString(), e.getEntity().getUniqueId().toString()));
    }

    public static void getLootingLevel(){
        Map<Enchantment, Integer> s = Objects.requireNonNull(ede.getEntity().getKiller()).getInventory().getItemInMainHand().getEnchantments();
        for (Map.Entry<Enchantment,Integer> entry : s.entrySet()){
            if (entry.getKey().toString().contains("looting")){
                Integer lootingLevel = entry.getValue();
            }
        }
    }

    public static void giveMoneyCheck(Player pa, Entity e){
        int counter = 0;
        if (pa.hasPermission("m4m.rewardMoney") || pa.isOp() || pa.hasPermission("m4m.rewardmoney")) {
            for (MobSpawnedReason mobSpawnedReason : msr) {
                if (mobSpawnedReason.getUuid().equals(e.getUniqueId().toString())) {
                    counter = 1;
                    if (mobSpawnedReason.getMobSpawnReason().equalsIgnoreCase("SPAWNER_EGG")) {
                        Boolean spawnEggs = MobConfigManager.mobsCfg.getBoolean("spawneggs");
                        if (Boolean.TRUE.equals(spawnEggs)) {
                            giveMoney = true;
                        } else {
                            giveMoney = false;
                        }
                    } else if (mobSpawnedReason.getMobSpawnReason().equalsIgnoreCase("SPAWNER")) {
                        Boolean spawners = MobConfigManager.mobsCfg.getBoolean("spawners");
                        if (Boolean.TRUE.equals(spawners)) {
                            giveMoney = true;
                        } else {
                            giveMoney = false;
                        }
                    }
                }
            }
            if(counter == 0){
                giveMoney = true;
            }
            List<Mobs4MoneyPlayer> pl = Money4Mobs.getPlayerList();
            for (int i = 0; i < Money4Mobs.getPlayerList().size(); i++){
                if (e.getName().equalsIgnoreCase(pl.get(i).playerName)) {
                    giveMoney = false;
                }
            }

            if(e instanceof Player) {
                giveMoney = true;
            }
        } else {
            giveMoney = false;
        }
    }



    public static void setRange(Entity e, Player pa){
        double level1 = MobConfigManager.mobsCfg.getDouble("group-multiplier.level-1");
        double level2 = MobConfigManager.mobsCfg.getDouble("group-multiplier.level-2");
        double level3 = MobConfigManager.mobsCfg.getDouble("group-multiplier.level-3");
        double level4 = MobConfigManager.mobsCfg.getDouble("group-multiplier.level-4");
        double level5 = MobConfigManager.mobsCfg.getDouble("group-multiplier.level-5");
        double operator = MobConfigManager.mobsCfg.getDouble("group-multiplier.operator");
        double multiplier = 1;
        if (pa.hasPermission("m4m.multiplier.level-5")) {
            multiplier = level5;
        }
        else if (pa.hasPermission("m4m.multiplier.level-4")) {
            multiplier = level4;
        }
        else if (pa.hasPermission("m4m.multiplier.level-3")) {
            multiplier = level3;
        }
        else if (pa.hasPermission("m4m.multiplier.level-2")) {
            multiplier = level2;
        }
        else if (pa.hasPermission("m4m.multiplier.level-1")) {
            multiplier = level1;
        }
        else {
            multiplier = 1;
        }
        if (pa.isOp()){
            multiplier = operator;
        }
        for (MobModel mobModel : mm) {
            String entity = "Craft" + mobModel.getMobName();
            String es = e.toString();
            if(e instanceof Player) {
                es = "CraftPlayer";
            }
            Integer lowWorth = mobModel.getLowWorth();
            Integer highWorth = mobModel.getHighWorth();
            if (es.contains(entity)) {
                money = mobModel.getHighWorth();
                money = (int) (Math.random() * (highWorth - lowWorth + 1) + lowWorth);
                money = money * multiplier;
            }
        }
    }


}
