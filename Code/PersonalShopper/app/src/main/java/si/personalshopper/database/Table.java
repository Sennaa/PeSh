package si.personalshopper.database;

import java.util.List;

/**
 * Created by Senna on 11-5-2016.
 */
public interface Table<T> {

    // Add new item to table
    public void add(T t);

    // Get item with index i
    public T getIndex(int id);

    // Get item that belongs to certain String
    public T get(String s);

    // Get all items
    public List<T> getAll();

    // Get size
    public int getSize();

    // Update single item
    public int update(T t);

    // Delete single item
    public void delete(T t);

}
