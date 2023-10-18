package com.e2g16.quizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.e2g16.quizapp.databinding.ActivityQuizBinding
import com.e2g16.quizapp.model.Question
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class QuizActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityQuizBinding.inflate(layoutInflater)
    }

    var currentQuestion = 0
    var score = 0

    private lateinit var questionList: ArrayList<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        questionList = ArrayList()

        var image = intent.getIntExtra("categoryimg", 0)
        var catText = intent.getStringExtra("questionType")
        Firebase.firestore.collection("Questions")
            .document(catText.toString())
            .collection("question1")
            .get()
            .addOnSuccessListener { questionData ->
                questionList.clear()
                for (data in questionData.documents) {
                    var question: Question? = data.toObject(Question::class.java)
                    questionList.add(question!!)
                }

                if (questionList.size > 0) {
                    binding.question.text = questionList[currentQuestion].question
                    binding.option1.text = questionList[currentQuestion].option1
                    binding.option2.text = questionList[currentQuestion].option2
                    binding.option3.text = questionList[currentQuestion].option3
                    binding.option4.text = questionList[currentQuestion].option4
                }
            }

        binding.categoryimg.setImageResource(image)

        binding.CoinWithdrawal.setOnClickListener {
            var bottomSheetDialog: BottomSheetDialogFragment = Withdrawal()
            bottomSheetDialog.show(this@QuizActivity.supportFragmentManager, "TEST")
            bottomSheetDialog.enterTransition
        }
        binding.CoinWithdrawal1.setOnClickListener {
            var bottomSheetDialog: BottomSheetDialogFragment = Withdrawal()
            bottomSheetDialog.show(this@QuizActivity.supportFragmentManager, "TEST")
            bottomSheetDialog.enterTransition
        }

        binding.option1.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option1.text.toString())
        }
        binding.option2.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option2.text.toString())
        }
        binding.option3.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option3.text.toString())
        }
        binding.option4.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option4.text.toString())
        }
    }

    private fun nextQuestionAndScoreUpdate(str: String) {

        if (str.equals(questionList[currentQuestion].ans)) {
            score += 10
            Toast.makeText(this, score.toString(), Toast.LENGTH_SHORT).show()
        }

        currentQuestion++

        var passScore = (questionList.size * 10) / 2

        if (currentQuestion >= questionList.size) {
            if (score >= passScore) {
                binding.winner.visibility = View.VISIBLE
            } else {
                binding.sorry.visibility = View.VISIBLE
            }

            Toast.makeText(this, "You have reached the end of the quiz", Toast.LENGTH_SHORT).show()

        } else {
            binding.question.text = questionList[currentQuestion].question
            binding.option1.text = questionList[currentQuestion].option1
            binding.option2.text = questionList[currentQuestion].option2
            binding.option3.text = questionList[currentQuestion].option3
            binding.option4.text = questionList[currentQuestion].option4
        }
    }

}