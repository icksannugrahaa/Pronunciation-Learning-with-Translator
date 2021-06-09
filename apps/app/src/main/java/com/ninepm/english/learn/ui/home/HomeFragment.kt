package com.ninepm.english.learn.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ninepm.english.learn.R
import com.ninepm.english.learn.databinding.FragmentHomeBinding
import com.ninepm.english.learn.firebase.auth.FirebaseAuthConfig
import com.ninepm.english.learn.firebase.auth.FirebaseAuthConfig.Companion.auth
import com.ninepm.english.learn.ui.login.LoginActivity
import com.ninepm.english.learn.ui.question.BasicQuestionActivity
import com.ninepm.english.learn.ui.question.QuestionsActivity
import com.ninepm.english.learn.utils.AnimationUtils.Companion.setAnimFlyUp
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            val factory = ViewModelFactory.getInstance(requireContext())
            viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

            binding.btnTry.setOnClickListener {
                Intent(requireContext(), QuestionsActivity::class.java).apply {
                    startActivity(this)
                }
            }
            binding.btnLogin.setOnClickListener {
                Intent(requireContext(), LoginActivity::class.java).apply {
                    startActivity(this)
                }
            }

            setAnim()
            checkLogin()
        }
    }

    override fun onResume() {
        super.onResume()
        checkLogin()
    }

    private fun checkLogin() {
        with(binding) {
            viewModel.getUser().observe(viewLifecycleOwner, { response ->
                Log.d("firebase_response_user", response.toString())
                if (response.uid != null && response.isVerified!!) {
                    txtTitle.text = "Selamat Datang,"
                    txtSubtitle.text = response.username
                    btnLogin.text = "Logout"
                    btnLogin.setOnClickListener {
                        FirebaseAuthConfig.signOut()
                        Intent(requireContext(), LoginActivity::class.java).apply {
                            startActivity(this)
                        }
                    }
                } else {
                    with(binding) {
                        txtTitle.text = resources.getString(R.string.let_start)
                        txtSubtitle.text = resources.getString(R.string.login_to_save_your_progress)
                        btnLogin.text = resources.getString(R.string.login)
                    }
                }
            })
        }
    }

    private fun getBackgroundHeight(): Float {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        return if (displayMetrics.widthPixels <= 720) {
            -1000F
        } else {
            -1370F
        }
    }

    private fun setAnim() {
        binding.apply {
            bgSplashscreen.animate().translationY(getBackgroundHeight())
                .setDuration(600).startDelay = 400
            txtIntroduce.animate().translationY(140F).alpha(0F).startDelay = 400
            GlobalScope.launch {
                startAnimDelay(600)
            }
        }
    }

    private suspend fun startAnimDelay(length: Long) = withContext(Dispatchers.Main) {
        delay(length)
        binding.apply {
            txtTitle.setAnimFlyUp(requireContext())
            txtSubtitle.setAnimFlyUp(requireContext())
            txtOr.setAnimFlyUp(requireContext())
            btnTry.setAnimFlyUp(requireContext())
            btnLogin.setAnimFlyUp(requireContext())
            imgBg.setAnimFlyUp(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}