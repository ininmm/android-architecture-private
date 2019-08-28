package com.ininmm.todoapp.di.module

import com.ininmm.todoapp.ui.statistics.StatisticsFragment
import dagger.Binds
import dagger.Module

@Module
abstract class StatisticsFragmentModule {

    @Binds
    abstract fun contributeStatisticsFragment(fragment: StatisticsFragment): StatisticsFragment
}