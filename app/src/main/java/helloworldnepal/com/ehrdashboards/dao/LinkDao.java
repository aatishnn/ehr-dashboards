package helloworldnepal.com.ehrdashboards.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import helloworldnepal.com.ehrdashboards.entity.Link;

import java.util.List;

@Dao
public interface LinkDao {

    @Query("SELECT * FROM link")
    List<Link> getAll();

    @Query("SELECT COUNT(*) from link")
    int countLinks();

    @Insert
    void insertAll(Link... links);

    @Delete
    void delete(Link link);
}