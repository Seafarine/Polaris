//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Iterator;

public class GameProfileBanList extends JsonList<GameProfile, GameProfileBanEntry> {
    public GameProfileBanList(File var1) {
        super(var1);
    }

    protected JsonListEntry<GameProfile> a(JsonObject var1) {
        return new GameProfileBanEntry(var1);
    }

    public boolean isBanned(GameProfile var1) {
        return this.d(var1);
    }

    public String[] getEntries() {
        String[] var1 = new String[this.e().size()];
        int var2 = 0;

        GameProfileBanEntry var4;
        for(Iterator var3 = this.e().values().iterator(); var3.hasNext(); var1[var2++] = ((GameProfile)var4.getKey()).getName()) {
            var4 = (GameProfileBanEntry)var3.next();
        }

        return var1;
    }

    protected String b(GameProfile var1) {
        return var1.getId().toString();
    }

    public GameProfile a(String var1) {
        Iterator var2 = this.e().values().iterator();

        GameProfileBanEntry var3;
        do {
            if (!var2.hasNext()) {
                return null;
            }

            var3 = (GameProfileBanEntry)var2.next();
        } while(!var1.equalsIgnoreCase(((GameProfile)var3.getKey()).getName()));

        return (GameProfile)var3.getKey();
    }
}
