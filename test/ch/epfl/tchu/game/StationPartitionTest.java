package ch.epfl.tchu.game;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import static ch.epfl.tchu.game.ChMap.*;

public class StationPartitionTest {
    private static Station BADEN = stations().get(0);
    private static Station BALE = stations().get(1);
    private static Station BELLINZONE = stations().get(2);
    private static Station BERNE = stations().get(3);
    private static Station BRIG = stations().get(4);
    private static Station BRUZIO = stations().get(5);
    private static Station COIR = stations().get(6);

    @Test
    void builderWorks() {
        StationPartition.Builder builder = new StationPartition.Builder(10);
        builder.connect(BADEN, BALE);
        // repr bale = baden
        builder.connect(BELLINZONE, BALE);
        // repr de baden = bellinzone
        builder.connect(BERNE, BRUZIO);
        // repr de bruzio = berne
        builder.connect(BRIG, BRUZIO);
        // repr berne = brig
        builder.connect(BRUZIO, COIR);
        // repr coir = bruzio

        StationPartition partition = builder.build();

        assertTrue(partition.connected(BADEN, BALE));
        assertTrue(partition.connected(BADEN, BELLINZONE));
        assertTrue(partition.connected(BALE, BELLINZONE));
        assertTrue(partition.connected(BELLINZONE, BALE));
        assertTrue(partition.connected(COIR, BRIG));
        assertFalse(partition.connected(BRUZIO, BELLINZONE));

    }
}













