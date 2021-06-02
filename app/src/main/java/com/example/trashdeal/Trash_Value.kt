package com.example.trashdeal

class Trash_Value(val id: String, val waste_weight: String, val waste_type: String, var waste_points: Int) {
    constructor() : this("","","",0)
}