package com.ininmm.todoapp.di.module.ui.statistics

import com.ininmm.todoapp.ui.statistics.StatisticsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class StatisticsModule {

    @ContributesAndroidInjector
    internal abstract fun contributeStatisticsFragment(): StatisticsFragment
}