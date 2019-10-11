package com.ininmm.todoapp.di

import android.content.Context
import com.ininmm.todoapp.TodoApplication
import com.ininmm.todoapp.di.module.ApplicationModule
import com.ininmm.todoapp.di.module.data.DataModule
import com.ininmm.todoapp.di.module.ui.ActivityBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        ActivityBindingModule::class,
        ViewModelBuilder::class,
        DataModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<TodoApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): ApplicationComponent
    }
}