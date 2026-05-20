package io.github.sheepdestroyer.materialisheep;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FullscreenViewModel extends ViewModel {
    private final MutableLiveData<Boolean> fullscreenEvent = new MutableLiveData<>();

    public LiveData<Boolean> getFullscreenEvent() {
        return fullscreenEvent;
    }

    public void setFullscreen(boolean fullscreen) {
        fullscreenEvent.setValue(fullscreen);
    }
}
