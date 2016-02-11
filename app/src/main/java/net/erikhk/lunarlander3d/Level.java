package net.erikhk.lunarlander3d;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by kl on 2016-02-12.
 */
public class Level {
    public int num=1;
    Terrain t;
    ArrayList<Coin> coins;
    LandingPoint landingPoint;


    public Level(Context c)
    {
        coins = new ArrayList<Coin>();
        t = new Terrain(c);


    }

}
