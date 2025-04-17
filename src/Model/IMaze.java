package Model;

import java.util.List;

public interface IMaze {
    ICave getRandomEmptyCave();
    List<ICave> getAllCaves();
    ICave getCaveById(int id);
}
