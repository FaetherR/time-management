package com.example.timemanagement.adapter

interface OnClickInsert {
    fun onClickInsert()
    fun onDisableClick(position: Int){}
    fun onClickBtnEdit(){}
    fun onClickItem(){}
}