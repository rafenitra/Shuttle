package com.simplecity.amp_library.glide.palette;

import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.Resource;

public class ColorSetResource implements Resource<ColorSet> {
    private final ColorSet colorSet;

    public ColorSetResource(@NonNull ColorSet colorSet) {
        this.colorSet = colorSet;
    }

    @Override
    public ColorSet get() {
        return colorSet;
    }

    @Override
    public int getSize() {
        return ColorSet.Companion.estimatedSize();
    }

    @Override
    public void recycle() {
        // Cette méthode est vide car son implémentation n'a pas encore été fournie.
        // Elle sera mise en œuvre dans une version ultérieure lorsque nécessaire.
        throw new UnsupportedOperationException("La méthode recycler() doit être implémentée.");
    }
}