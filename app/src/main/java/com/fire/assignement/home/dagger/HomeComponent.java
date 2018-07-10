package com.fire.assignement.home.dagger;


import com.fire.assignement.dagger.AppComponent;
import com.fire.assignement.home.mvp.HomeActivity;

import dagger.Component;

@HomeScope
@Component(modules = HomeModule.class, dependencies = AppComponent.class)
public interface HomeComponent {
    void inject(HomeActivity homeActivity);
}
