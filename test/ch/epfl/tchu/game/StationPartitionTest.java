package ch.epfl.tchu.game;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import static ch.epfl.tchu.game.ChMap.*;

public class StationPartitionTest {
    private static Station BADEN = stations().get(0);
    private static Station BALE = stations().get(1);
    private static Station BELLINZONE = stations().get(2);
    private static Station BERNE = stations().get(3);

    @Test
    void builderWorks(){
        StationPartition.Builder builder = new StationPartition.Builder(10);

        builder.connect(BADEN, BALE);
        builder.connect(BELLINZONE, BALE);
        StationPartition partition = builder.build();

        assertTrue(partition.connected(BADEN, BALE));
        assertTrue(partition.connected(BADEN, BELLINZONE));
        assertTrue(partition.connected(BALE, BELLINZONE));

        //assertFalse(partition.connected(BADEN, BELLINZONE));
    }
}
