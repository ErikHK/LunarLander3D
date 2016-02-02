package net.erikhk.lunarlander3d;

import android.content.Context;

/**
 * Created by kl on 2016-02-01.
 */
public class Sky {

    public Model m;

    public Sky(Context c)
    {
        m = new Model(c, R.raw.plane_verts, R.raw.plane_normals);

    }

    public void DrawModel()
    {
        m.DrawModel();
    }
}
