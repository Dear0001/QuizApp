package com.e2g16.quizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.e2g16.quizapp.databinding.ActivityQuizBinding
import com.e2g16.quizapp.model.Question
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class QuizActivity : AppCompatActivity() {

    private val binding by lazy { ActivityQuizBinding.inflate(layoutInflater) }

    var currentChance = 0L
    var currentQuestion = 0
    var score = 0

    private lateinit var questionList: ArrayList<Question>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Firebase.database.reference.child("PlayChance")
            .child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value
                    if (value != null) {
                        currentChance = value as? Long ?: 0
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("TAG", "onCancelled: ${error.message}")
                }
            })

        Firebase.database.reference.child("PlayerCoin")
            .child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value
                    if (value != null) {
                        binding.coinWithdrawal.text = snapshot.value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO()
                }
            })

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

                // Shuffle the question list
                questionList.shuffle()

                if (questionList.size > 0) {
                    binding.question.text = questionList[currentQuestion].question
                    binding.option1.text = questionList[currentQuestion].option1
                    binding.option2.text = questionList[currentQuestion].option2
                    binding.option3.text = questionList[currentQuestion].option3
                    binding.option4.text = questionList[currentQuestion].option4
                }
            }

        binding.categoryimg.setImageResource(image)

        binding.coinWithdrawal.setOnClickListener {
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

        // Find the button and set an OnClickListener
        val nextQuestionButton = findViewById<Button>(R.id.nextQuestionButton)
        nextQuestionButton.setOnClickListener {
            nextQuestion()
        }
    }

    // Function for moving to the next question
    private fun nextQuestion() {
        currentQuestion++

        if (currentQuestion < questionList.size) {
            binding.question.text = questionList[currentQuestion].question
            binding.option1.text = questionList[currentQuestion].option1
            binding.option2.text = questionList[currentQuestion].option2
            binding.option3.text = questionList[currentQuestion].option3
            binding.option4.text = questionList[currentQuestion].option4
        } else {
            Toast.makeText(this, "You have reached the end of the quiz", Toast.LENGTH_SHORT).show()
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
                Firebase.database.reference.child("PlayChance")
                    .child(Firebase.auth.currentUser!!.uid).setValue(currentChance + 1)
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
