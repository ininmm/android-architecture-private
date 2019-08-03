package com.ininmm.todoapp.di

import android.content.Context
import com.ininmm.todoapp.TodoApplication
import com.ininmm.todoapp.di.module.ActivityModule
import com.ininmm.todoapp.di.module.ApplicationModule
import com.ininmm.todoapp.di.module.FragmentModule
import com.ininmm.todoapp.di.module.data.local.DatabaseModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        ActivityModule::class,
        FragmentModule::class,
        DatabaseModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<TodoApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}