package com.example.bmiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            bmiUI()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bmiUI(){
    /* 스낵바, 사이드메뉴의 기능을 수행하기 위한 코루틴 필요 */
    val scope = rememberCoroutineScope()

    /* 스낵바 상태를 저장 */
    val snackbarState = remember { SnackbarHostState() }

    /* 키, 몸무게 입력 상태를 기억할 변수 만들기 */
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    /* bmi 계산 결과를 기억할 변수 만들기 */
    var num by remember { mutableStateOf<Double?>(null) }

    /* UI 레이아웃 뼈대 만들기 */
    Scaffold(
        topBar = {
            TopAppBar(title = {Text("bmi계산기")})
        },
        /* 스낵바를 띄울 레이아웃 설정 */
        snackbarHost = { SnackbarHost(hostState = snackbarState) }
    )
    {
            innerPadding->
        Column (
            Modifier.padding(innerPadding).padding(20.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /* 어플 사용법을 보여줄 카드 컨테이너 */
            Card (
                colors = CardDefaults.cardColors(containerColor = Color.Yellow),  // 카드 배경색
                elevation = CardDefaults.cardElevation(3.dp),    // 카드 그림자
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Image(
                        painterResource(R.drawable.bmi),
                        contentDescription = "bmi이미지",
                        Modifier.fillMaxWidth().height(140.dp),
                        /* 카드에 알맞게 이미지 채우기 (화질 유지) */
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "키와 몸무게를 입력하여 계산하기 버튼을 클릭하시오.",
                        Modifier.padding(12.dp)
                    )
                }
            }

            /* 공백주기 */
            Spacer(Modifier.height(15.dp))

            /* 키를 입력할 텍스트 박스 추가 */
            OutlinedTextField(
                value = height,
                onValueChange = { newText -> height = newText },
                label = { Text("키 (cm)") },
                modifier = Modifier.fillMaxWidth()
            )
            /* 공백주기 */
            Spacer(Modifier.height(15.dp))

            /* 몸무게를 입력할 텍스트 박스 추가 */
            OutlinedTextField(
                value = weight,
                onValueChange = { newText -> weight = newText },
                label = { Text("몸무게 (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            /* 공백주기 */
            Spacer(Modifier.height(15.dp))

            /* 계산하기 버튼 추가 */
            Button(
                onClick = {
                    /* bmi 계산 수행 -> 무게 / {(키/100)*(키/100)} */
                    val h = height.toDoubleOrNull()
                    val w = weight.toDoubleOrNull()
                    if(h!=null&&w!=null&&h>0.0){
                        val meter = h/100.0
                        num = w/(meter*meter)
                    }
                    else{
                        num = null
                        /* 키와와 몸무게를 입력하지 않으면 스낵바 경고 메시지 띄우기 */
                        scope.launch { snackbarState.showSnackbar("키와 몸무게를 입력하세요!!") }
                    }
                },
                Modifier.fillMaxWidth()
            ) {
                 Text("계산하기")
            }

            /* 공백주기 */
            Spacer(Modifier.height(15.dp))

            /* bmi 결과 보여주기 */
            if(num!=null){
                val result = num!!
                Text("BMI : ${"%.2f".format(result)}")
                /* 공백주기 */
                Spacer(Modifier.height(8.dp))
                Text("판정결과 : ${bmiResult(result)}")
            }

        }
    }
}

/* bmi 결과를 보여주는 함수 */
// - 매개변수 : 계산된 bmi (소수)
// - 결과타입 : 문자열 (저체중, 정상, 과체중, 비만)
// - 결과조건(when) : 18.5미만(저체중), 23미만(정상), 25미만(과체중), 그 이상(비만)
fun bmiResult(bmi: Double): String = when {
    bmi < 18.5 -> "저체중"
    bmi < 23 -> "정상"
    bmi < 25 -> "과체중"
    else -> "비만"
}