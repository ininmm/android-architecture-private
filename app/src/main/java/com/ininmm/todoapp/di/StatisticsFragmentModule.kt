package com.ininmm.todoapp.di

import com.ininmm.todoapp.ui.statistics.StatisticsFragment
import dagger.Binds
import dagger.Module

@Module
abstract class StatisticsFragmentModule {

    @Binds
    abstract fun contributeStatisticsFragment(fragment: StatisticsFragment): StatisticsFragment
}