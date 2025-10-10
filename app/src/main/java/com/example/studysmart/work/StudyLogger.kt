package com.example.studysmart.work

interface StudyLogger {
    fun scheduleDailyAggregation()  // 每日定时聚合
    fun cancelDailyAggregation()
}
