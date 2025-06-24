package com.arakene.presentation.util

import com.arakene.domain.repository.WidgetRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetModelRepositoryEntryPoint {
    fun widgetModelRepository(): WidgetRepository
}

//companion object {
//    fun get(applicationContext: Context): WidgetRepository {
//        var widgetModelRepositoryEntryPoint: WidgetModelRepositoryEntryoint = EntryPoints.get(
//            applicationContext,
//            WidgetModelRepositoryEntryoint::class.java,
//        )
//        return widgetModelRepositoryEntryPoint.widgetModelRepository()
//    }
//}
