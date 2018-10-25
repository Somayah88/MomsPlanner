package com.example.somayahalharbi.momsplanner.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class MomsPlannerRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MomsPlannerRemoteViewFactory(this.getApplicationContext(), intent);
    }
}
