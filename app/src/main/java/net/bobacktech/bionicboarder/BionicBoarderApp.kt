package net.bobacktech.bionicboarder

import android.app.Application
import net.bobacktech.bionicboarder.utils.MissionClock
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

val bbModule = module {
    single { MissionClock() }
}


class BionicBoarderApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@BionicBoarderApp)
            // Load modules
            modules(bbModule)
        }

    }
}