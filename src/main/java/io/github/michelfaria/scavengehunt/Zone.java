package io.github.michelfaria.scavengehunt;

import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    private String name;
    private List<Block> eggs;
    private int x1;
    private int y1;
    private int z1;
    private int x2;
    private int y2;
    private int z2;
    private List<Block> collected = new ArrayList<>();

    public Zone(String name, List<Block> eggs, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.name = name;
        this.eggs = eggs;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public List<Block> getEggs() {
        return eggs;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "name='" + name + '\'' +
                ", eggs=" + eggs +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", z1=" + z1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", z2=" + z2 +
                ", collected=" + collected +
                '}';
    }

    public void addFound(Block egg) {
        if (!eggs.contains(egg)) {
            throw new IllegalArgumentException("Egg not present in list of eggs");
        }
        eggs.remove(egg);
        collected.add(egg);
    }

    public String getName() {
        return name;
    }
}
