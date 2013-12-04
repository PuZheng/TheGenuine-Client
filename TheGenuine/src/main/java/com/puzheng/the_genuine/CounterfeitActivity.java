package com.puzheng.the_genuine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.puzheng.the_genuine.views.NavBar;

public class CounterfeitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counterfeit);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CounterfeitActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });

        NavBar navBar = (NavBar) findViewById(R.id.navBar);
        navBar.setContext(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.counterfeit, menu);
        return true;
    }
    
}
