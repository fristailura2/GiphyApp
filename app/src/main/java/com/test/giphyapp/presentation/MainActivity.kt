package com.test.giphyapp.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.test.giphyapp.presentation.base.*
import com.test.giphyapp.presentation.screens.details.DetailsScreen
import com.test.giphyapp.presentation.screens.details.DetailsViewModel
import com.test.giphyapp.presentation.screens.main.MainScreen
import com.test.giphyapp.presentation.screens.main.MainViewModel
import com.test.giphyapp.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppEntryPoint(imageLoader)
        }
    }
}

val LocalImageLoader = compositionLocalOf<ImageLoader> { error("Not init") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppEntryPoint(imageLoader: ImageLoader) {
    AppTheme {
        Scaffold {
            ProvideLocalDependencies(imageLoader) {
                Navigation(Modifier.padding(it))
            }
        }
    }
}

@Composable
fun ProvideLocalDependencies(
    imageLoader: ImageLoader,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalImageLoader provides imageLoader
    ) {
        content()
    }
}

@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val controller = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = controller,
        startDestination = MainNavGraph.Main.getFullRoute(),
        builder = {
            detailsScreen(controller)
            mainScreen(controller)
        })

}

fun NavGraphBuilder.mainScreen(controller: NavController) {
    composable(MainNavGraph.Main) {
        val viewModel: MainViewModel = hiltViewModel(it)
        MainScreen(viewModel = viewModel)
        viewModel.SetUpNavigation(it) { route ->
            when (route) {
                is MainViewModel.Route.Details -> {
                    val res = MainNavGraph.Details.getParamPathBuilder().apply {
                        addParam(MainNavGraph.Details.indexParam to route.itemIndex)
                        addParam(MainNavGraph.Details.searchTextParam to route.requestText)
                    }.build()
                    controller.navigate(res)
                }
            }
        }
    }
}

fun NavGraphBuilder.detailsScreen(controller: NavController) {
    composable(
        MainNavGraph.Details,
    ) {
        val viewModel: DetailsViewModel = hiltViewModel(it)
        DetailsScreen(viewModel = viewModel)
        viewModel.SetUpNavigation(it) { route ->
            when (route) {
                DetailsViewModel.Route.Back -> controller.popBackStack()
            }
        }
    }
}



