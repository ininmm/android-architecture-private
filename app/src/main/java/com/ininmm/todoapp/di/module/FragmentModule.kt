package com.ininmm.todoapp.di.module

import com.ininmm.todoapp.ui.statistics.StatisticsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector(modules = [StatisticsFragmentModule::class])
    abstract fun contributeStatisticsFragment(): StatisticsFragment
}