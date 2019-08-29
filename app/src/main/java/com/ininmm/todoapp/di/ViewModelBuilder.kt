package com.ininmm.todoapp.di

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
internal abstract class ViewModelBuilder {
    @Binds
    internal abstract fun bindTodoViewModelFactory(factory: TodoViewModelFactory): ViewModelProvider.Factory
}