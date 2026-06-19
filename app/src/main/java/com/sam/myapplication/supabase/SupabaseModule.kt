package com.sam.myapplication.supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime

object SupabaseModule {
    const val SUPABASE_URL = "https://uhmjgnwpzsemksxcjpiz.supabase.co"
    // IMPORTANT: Replace this with your LONG "anon public" key from Supabase Settings > API
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVobWpnbndwenNlbWtzeGNqcGl6Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3ODMyNDU2NCwiZXhwIjoyMDkzOTAwNTY0fQ.SVdNjQlocvBEwwxl2K_7BMT94vUq5fkNRev65fXrvyc"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}
