package com.example.a0104.crolling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ActionProvider;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MakeList extends BroadcastReceiver {
    String[][] TimeTable;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("makeList","broadcast 호출");
        ArrayList<String> SubList = intent.getStringArrayListExtra("SubList");
        Log.d("makeList",SubList.get(0).toString());
        Sort(SubList);
        Log.d("makeList","broadcast 끝");
    }

    public void Sort(ArrayList<String> SubList) {
        Log.d("sort","정렬 호출");
        for (int i = 0; i < SubList.size(); i++) { //강의마다 시간표에 넣는 작업
            String Sub = SubList.get(i).toString(); //강의 넣기
            String Subject[] = Sub.split(" "); //강의 이름만 추출
            i++; //넣은 과목의 시간으로 이동
            String timeCheck = SubList.get(i).toString(); //강의 시간 넣기
            String[] time = timeCheck.split(" "); //강의 시간이 2개이상일 경우 분리한다.

            for(int j=0; j < time.length; j++){ // 강의시간 하나를 분류한다.
                int day = 0, cloStart, cloEnd, clo, minStart, minEnd, min; // 요일, 시간, 분으로 분류
                switch (time[i].charAt(0)){
                    case '일' : day = 0;
                        break;
                    case '월' : day = 1;
                        break;
                    case '화' : day = 2;
                        break;
                    case '수' : day = 3;
                        break;
                    case '목' : day = 4;
                        break;
                    case '금' : day = 5;
                        break;
                    case '토' : day = 6;
                        break;
                    default: break;
                }
                cloStart = Integer.parseInt(time[i].substring(2,4)); //문자형을 상수형으로 바꿔주고 시작시간에 넣는다.
                cloEnd = Integer.parseInt(time[i].substring(8,10)); // " 종료시간에 넣는다.
                clo = cloEnd - cloStart; // 진행시간을 계산
                cloStart -= cloStart -8; // 시간에 8을 빼서 강의를 교시마다 넣을 수 있도록한다.

                //시작분은 00,30분 단위로 끊어져 있다. 종료분은 15,45,50으로 되어있어 복잡하다.
                minStart = Integer.parseInt(time[i].substring(5,7)); // " 시작분
                minEnd = Integer.parseInt(time[i].substring(11,13)); // " 종료분

                while(clo == 0) {
                    TimeTable[day][cloStart] = Subject[i];
                }
            }
        }
    }
}
