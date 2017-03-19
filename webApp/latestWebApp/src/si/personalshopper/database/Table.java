/*
 * Decompiled with CFR 0_118.
 */
package si.personalshopper.database;

import java.util.List;

public interface Table<T> {
    public void insert(T var1);

    public T getIndex(int var1);

    public T get(String var1);

    public List<T> getAll();

    public int getSize();

    public void update(T var1);

    public void delete(T var1);
}

