package Latch.Money4Mobs;

import java.util.ArrayList;
import java.util.List;

public class SetMobList {

    private static List<MobModel> mobList = new ArrayList<MobModel>();

    public List<MobModel> getMobModel() {
        setMobWorth();
        return mobList;
    }

    public void setMobWorth(){
        mobList.add(new MobModel("Bat", 10, 10, true, false, null));
        mobList.add(new MobModel("Blaze", 25, 25,  true,false, null));
        mobList.add(new MobModel("Cat", 1, 1,  true,false, null));
        mobList.add(new MobModel("CaveSpider", 15, 15, true, false, null));
        mobList.add(new MobModel("Chicken", 2, 2, true, false, null));
        mobList.add(new MobModel("Cow", 2, 2, false, true, null));
        mobList.add(new MobModel("Creeper", 25, 25, true, false, null));
        mobList.add(new MobModel("Donkey", 100, 100, true, false, null));
        mobList.add(new MobModel("ElderGuardian", 300, 300, true, false, null));
        mobList.add(new MobModel("EnderDragon", 10000, 10000,  true, false, null));
        mobList.add(new MobModel("Enderman", 25, 25, true, false, null));
        mobList.add(new MobModel("Endermite", 300, 300, true, false, null));
        mobList.add(new MobModel("Ghast", 30, 30, true, false, null));
        mobList.add(new MobModel("Giant", 1, 1, true, false, null));
        mobList.add(new MobModel("Guardian", 75, 75, true, false, null));
        mobList.add(new MobModel("IronGolem", 10, 10, true, false, null));
        mobList.add(new MobModel("MagmaCube", 12, 12, true, false, null));
        mobList.add(new MobModel("Mule", 30, 30, true, false, null));
        mobList.add(new MobModel("MushroomCow", 3, 3, true, false, null));
        mobList.add(new MobModel("Ocelot", 25, 25, true, false, null));
        mobList.add(new MobModel("Pig", 3, 3, true, false, null));
        mobList.add(new MobModel("PigZombie", 45, 45, true, false, null));
        mobList.add(new MobModel("Player", 25, 25, true, false, null));
        mobList.add(new MobModel("Rabbit", 15, 15, true, false, null));
        mobList.add(new MobModel("Sheep", 3, 3, true, false, null));
        mobList.add(new MobModel("Silverfish", 30, 30, true, false, null));
        mobList.add(new MobModel("Skeleton", 15, 15, true, false, null));
        mobList.add(new MobModel("SkeletonHorse", 150, 150, true, false, null));
        mobList.add(new MobModel("Slime", 12, 12, true, false, null));
        mobList.add(new MobModel("Snowman", 100, 100, true, false, null));
        mobList.add(new MobModel("Spider", 15, 15, true, false, null));
        mobList.add(new MobModel("Squid", 10, 10, true, false, null));
        mobList.add(new MobModel("Villager", 5, 5, true, false, null));
        mobList.add(new MobModel("Witch", 60, 60, true, false, null));
        mobList.add(new MobModel("Wither", 2500, 2500, true, false, null));
        mobList.add(new MobModel("WitherSkeleton", 45, 45, true, false, null));
        mobList.add(new MobModel("Wolf", 25, 25, true, false, null));
        mobList.add(new MobModel("Zombie", 15, 25, true, false, null));
        mobList.add(new MobModel("ZombieHorse", 35, 35, true, false, null));
        mobList.add(new MobModel("ZombieVillager", 20, 20, true, false, null));
    }



}
