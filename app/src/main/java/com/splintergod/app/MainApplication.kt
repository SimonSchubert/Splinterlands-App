package com.splintergod.app

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.splintergod.app.abilities.AbilitiesViewModel
import com.splintergod.app.balances.BalancesViewModel
import com.splintergod.app.carddetail.CardDetailViewModel
import com.splintergod.app.collection.CollectionViewModel
import com.splintergod.app.login.LoginViewModel
import com.splintergod.app.rewards.RewardsViewModel
import com.splintergod.app.rulesets.RulesetsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class MainApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }

    private val appModule = module {
        single {
            Cache(androidContext())
        }
        single {
            Requests(get())
        }
        single {
            Session(get())
        }
        viewModel { MainActivityViewModel(get(), get(), get()) }
        viewModel { LoginViewModel(get(), get(), get()) }
        viewModel { RulesetsViewModel(get(), get()) }
        viewModel { AbilitiesViewModel(get()) }
        viewModel { RewardsViewModel(get(), get(), get()) }
        viewModel { BalancesViewModel(get(), get(), get()) }
        viewModel { CollectionViewModel(get(), get(), get()) }
        viewModel { CardDetailViewModel(get(), get(), get()) }
        viewModel { CardDetailViewModel(get(), get(), get()) }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }

}