package edu.victorlamas.apirestwords.data

import edu.victorlamas.apirestwords.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetWordsUseCase (
    private val repository: WordsRepository
) {
    operator fun invoke(): Flow<List<Word>> {
        return combine(
            repository.getAllApiWords(),
            repository.getSortedFavWords()
        ) { apiWords, favWords ->
            apiWords.map { apiWord ->
                favWords.find { favWord -> favWord.word == apiWord.word }?.let {
                    apiWord.favourite = true
                }
                apiWord
            }
        }
    }
}