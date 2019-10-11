package com.ininmm.todoapp.data.model

import com.google.gson.annotations.SerializedName

data class SearchRepo(
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    @SerializedName("items")
    val items: List<Item>,
    @SerializedName("total_count")
    val totalCount: Int
)

data class Item(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("default_branch")
    val defaultBranch: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("forks")
    val forks: Int,
    @SerializedName("git_url")
    val gitUrl: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("language")
    val language: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("node_id")
    val nodeId: String,
    @SerializedName("owner")
    val owner: Owner,
    @SerializedName("score")
    val score: Double,
    @SerializedName("size")
    val size: Int,
    @SerializedName("watchers")
    val watchers: Int
)

data class Owner(
    @SerializedName("id")
    val id: Int,
    @SerializedName("login")
    val login: String,
    @SerializedName("type")
    val type: String
)