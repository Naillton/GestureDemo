package com.example.gesturedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.gesturedemo.ui.theme.GestureDemoTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestureDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ClickDemo()
        // ClickInputGestureDemo()
        // DragDemo()
        // DragPointer()
        // ScrollableModifier()
        // ScrollableModifier1()
        MultiTouchDemo()
    }
}

/**
 * Usando um estado que armazena uma cor para ser trocada assim que o elemento for tocado,
 * o uso do clickable nao difere os toques na tela, por isso, toques simples ou duplos serao contados
 * como toques. O clickable pode ser usado para apps mais simples onde seria necessario como trocar a cor
 * de um Box.
 */

@Composable
fun ClickDemo() {

    var colorState by rememberSaveable { mutableStateOf(true) }

    var bgColor by remember { mutableStateOf(Color.Blue) }

    val clickHandler = {
        colorState = !colorState
        bgColor = if (colorState) {
            Color.Blue
        } else {
            Color.DarkGray
        }
    }

    Box(
        Modifier
            .clickable {
                clickHandler()
            }
            .background(bgColor)
            .size(200.dp)
    )

}

/**
 * Com o pointerInput e o detectTapGestures, podemos definir mais configuracaoes para toques do usuario
 * na tela, definimos funcoes para um unico toque, toques duplos, pressionando, pressionamento longo do usuario
 * a algum elemento da tela.
 */

@Composable
fun ClickInputGestureDemo() {
    var textState by rememberSaveable { mutableStateOf("Waiting ...") }
    val tapHandler = { it: String ->
        textState = it
    }

    Box(
        modifier = Modifier
            .padding(10.dp)
            .background(Color.Blue)
            .size(100.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { tapHandler("OnPress detected") },
                    onDoubleTap = { tapHandler("DoubleTap detected") },
                    onLongPress = { tapHandler("OnLongPress detected") },
                    onTap = { tapHandler("OnTap detected") }
                )
            }
    )
    Spacer(modifier = Modifier.height(10.dp))
    Text(text = textState)
}

/**
 * Detctando e aplicando gestos para arrastar componentes, tanto verticalmente como horizontalmente e
 * usando o modificador de estado rememberDraggableState() que sera usado para mover a posicao do elemento
 * com o gesto do usuario.
 */

@Composable
fun DragDemo() {
    var xOffset by remember { mutableStateOf(0f) }
    
    Box(
        modifier = Modifier
            // usando o offSet para determinar a posicao do elemento com base no estado
            // usando cordenadas x e y, o x mudara enquanto o y sempre permanecera em o na pocisao de origem
            .offset { IntOffset(xOffset.roundToInt(), 0) }
            .size(100.dp)
            .background(Color.Blue)
            // definindo orientacao de arrasto horizontal e o draggableState que tera um it float
            // representando a distancia que sera somada ao estado xOffset
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState {
                    xOffset += it
                }
            )
    )
}

/**
 * Combinando dragGestures com pointerInput
 */

@Composable
fun DragPointer() {
    var xOffset by remember { mutableStateOf(0f) }
    var yOffset by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(xOffset.roundToInt(), yOffset.roundToInt()) }
            .size(100.dp)
            .background(Color.Blue)
            .pointerInput(Unit) {
                detectDragGestures { _, it ->
                    xOffset += it.x
                    yOffset += it.y
                }
            }
    )
}

/**
 * Definindo rolagem com scrollable() para suportar gestos horizontais e verticais e gerenciando
 * estado com o rememberScrollableState().
 */

@Composable
fun ScrollableModifier() {
    // definindo estado float
    var offset by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // criando scrollable onde definimos a orientacao do scroll e o estado usado para definir
            // a posicao em que o scroll estara
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState {
                    offset += it
                    it
                }
            )
    ) {
        Box(modifier = Modifier
            .size(90.dp)
            .offset { IntOffset(0, offset.roundToInt()) }
            .background(Color.Red))
    }
}

/**
 * usando scrollable verticalmente e horizontalmente ao mesmo tempo.
 */

@Composable
fun ScrollableModifier1() {

    val image = ImageBitmap.imageResource(id = R.drawable.dms)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState())
    ) {
        Canvas(modifier = Modifier.size(360.dp, 270.dp)) {
            drawImage(
                image = image,
                topLeft = Offset(
                    x = 0f,
                    y = 0f
                )
            )
        }
    }
}

/**
 * Detectando gestos de pinca, gestos de rotacao
 */

@Composable
fun MultiTouchDemo() {
    // definindo estado para a escala quando utilizarmos o gesto de pinca
    var scale by rememberSaveable { mutableStateOf(1f) }
    // definindo angle para gesto de rotacao
    var angle by rememberSaveable { mutableStateOf(0f) }
    // definindo  gesto traslation onde nos poderemos mover o elemento na horizontal ou vertical
    var offset by remember { mutableStateOf(Offset.Zero) }

    // funcao state que tera 3 parametros de mudanca de gestos
    val state = rememberTransformableState {scaleChange, offsetChange, rotationChange ->
        scale *= scaleChange
        angle += rotationChange
        offset += offsetChange
    }

    Box(
        // definindo camada grafica para podermos usar os gestos
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = angle,
                translationX = offset.x,
                translationY = offset.y
            )
            .transformable(state = state)
            .background(Color.Blue)
            .size(100.dp)
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    GestureDemoTheme {
        MainScreen()
    }
}