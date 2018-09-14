package com.kandi.systemui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kandi.systemui.service.KandiSystemUiService;

public class Main extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentOne = new Intent(this, KandiSystemUiService.class);
        startService(intentOne);
    }
}
