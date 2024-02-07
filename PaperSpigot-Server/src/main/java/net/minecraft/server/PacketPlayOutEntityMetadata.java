//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.minecraft.server;

import java.io.IOException;
import java.util.List;

public class PacketPlayOutEntityMetadata implements Packet<PacketListenerPlayOut> {
    private int a;
    private List<DataWatcher.WatchableObject> b;

    public PacketPlayOutEntityMetadata() {
    }

    public PacketPlayOutEntityMetadata(int i, List<DataWatcher.WatchableObject> list) {
        this.a = i;
        this.b = list;
    }
    public PacketPlayOutEntityMetadata(int var1, DataWatcher var2, boolean var3) {
        this.a = var1;
        if (var3) {
            this.b = var2.c();
        } else {
            this.b = var2.b();
        }

    }

    public void a(PacketDataSerializer var1) throws IOException {
        this.a = var1.e();
        this.b = DataWatcher.b(var1);
    }

    public void b(PacketDataSerializer var1) throws IOException {
        var1.b(this.a);
        DataWatcher.a(this.b, var1);
    }

    public void a(PacketListenerPlayOut var1) {
        var1.a(this);
    }
}
