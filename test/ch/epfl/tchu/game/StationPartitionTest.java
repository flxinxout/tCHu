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
    private static Station COIRE = stations().get(6);
    private static Station DAVOS = stations().get(7);
    private static Station DELEMONT = stations().get(8);
    private static Station FRIBOURG = stations().get(9);
    private static Station GENEVE = stations().get(10);

    @Test
    void builderWorks() {
        StationPartition.Builder builder = new StationPartition.Builder(11);
        builder.connect(BADEN, BALE);
        // BADEN > BADEN / BADEN > BALE
        builder.connect(BELLINZONE, BALE);
        // BELLINZONE > BELLINZONE / BELLINZONE > BADEN / BADEN > BALE
        builder.connect(BALE, COIRE);
        // BELLINZONE > BELLINZONE / BELLINZONE > COIRE / BELLINZONE > BADEN / BADEN > BALE
        builder.connect(COIRE, BRUZIO);
        builder.connect(DAVOS, DELEMONT);
        builder.connect(DELEMONT, BERNE);
        builder.connect(DELEMONT, BERNE);
        builder.connect(BRIG, BERNE);
        builder.connect(BERNE, BADEN);
        builder.connect(FRIBOURG, GENEVE);

        StationPartition partition = builder.build();

        //Every of the 8 are connected
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                assertTrue(partition.connected(stations().get(i), stations().get(j)));
            }
        }


        assertFalse(partition.connected(BADEN, GENEVE));
        assertTrue(partition.connected(FRIBOURG, GENEVE));

        //Test out of bounds
        assertTrue(partition.connected(stations().get(20), stations().get(20)));
        assertFalse(partition.connected(stations().get(20), stations().get(22)));
    }
}













