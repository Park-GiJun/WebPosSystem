<!-- components/modals/CustomerRegisterModal.vue -->
<template>
  <TransitionRoot appear :show="true" as="template">
    <Dialog as="div" @close="closeModal" class="relative z-50">
      <TransitionChild
          enter="duration-300 ease-out"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="duration-200 ease-in"
          leave-from="opacity-100"
          leave-to="opacity-0"
      >
        <div class="fixed inset-0 bg-black/25" />
      </TransitionChild>

      <div class="fixed inset-0 overflow-y-auto">
        <div class="flex min-h-full items-center justify-center p-4">
          <TransitionChild
              enter="duration-300 ease-out"
              enter-from="opacity-0 scale-95"
              enter-to="opacity-100 scale-100"
              leave="duration-200 ease-in"
              leave-from="opacity-100 scale-100"
              leave-to="opacity-0 scale-95"
          >
            <DialogPanel class="w-full max-w-md transform overflow-hidden rounded-xl bg-white p-6 shadow-xl transition-all">
              <DialogTitle class="text-lg font-medium leading-6 text-gray-900 mb-4">
                {{ props.customer ? '고객 정보 수정' : '신규 고객 등록' }}
              </DialogTitle>

              <form @submit.prevent="handleSubmit" class="space-y-4">
                <!-- 고객명 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    고객명
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="text"
                      v-model="form.name"
                      :class="[
                      'w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500',
                      errors.name ? 'border-red-500' : 'border-gray-300'
                    ]"
                      @blur="validateField('name')"
                      required
                  />
                  <p v-if="errors.name" class="mt-1 text-sm text-red-500">
                    {{ errors.name }}
                  </p>
                </div>

                <!-- 전화번호 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    전화번호
                    <span class="text-red-500">*</span>
                  </label>
                  <input
                      type="tel"
                      v-model="form.phone"
                      placeholder="010-0000-0000"
                      :class="[
                      'w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500',
                      errors.phone ? 'border-red-500' : 'border-gray-300'
                    ]"
                      @blur="validateField('phone')"
                      required
                  />
                  <p v-if="errors.phone" class="mt-1 text-sm text-red-500">
                    {{ errors.phone }}
                  </p>
                </div>

                <!-- 생년월일 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    생년월일
                  </label>
                  <input
                      type="date"
                      v-model="form.birthDate"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <!-- 이메일 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    이메일
                  </label>
                  <input
                      type="email"
                      v-model="form.email"
                      :class="[
                      'w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500',
                      errors.email ? 'border-red-500' : 'border-gray-300'
                    ]"
                      @blur="validateField('email')"
                  />
                  <p v-if="errors.email" class="mt-1 text-sm text-red-500">
                    {{ errors.email }}
                  </p>
                </div>

                <!-- 주소 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    주소
                  </label>
                  <div class="space-y-2">
                    <div class="flex gap-2">
                      <input
                          type="text"
                          v-model="form.postcode"
                          placeholder="우편번호"
                          class="w-32 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                          readonly
                      />
                      <button
                          type="button"
                          @click="searchAddress"
                          class="px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
                      >
                        주소 검색
                      </button>
                    </div>
                    <input
                        type="text"
                        v-model="form.address"
                        placeholder="기본주소"
                        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        readonly
                    />
                    <input
                        type="text"
                        v-model="form.addressDetail"
                        placeholder="상세주소"
                        class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>

                <!-- 메모 -->
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-1">
                    메모
                  </label>
                  <textarea
                      v-model="form.note"
                      rows="3"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  ></textarea>
                </div>

                <!-- SMS 수신 동의 -->
                <div class="flex items-center gap-2">
                  <input
                      type="checkbox"
                      id="smsAgree"
                      v-model="form.smsAgree"
                      class="rounded text-blue-600 focus:ring-blue-500"
                  />
                  <label for="smsAgree" class="text-sm text-gray-700">
                    SMS 수신 동의
                  </label>
                </div>

                <!-- 버튼 영역 -->
                <div class="flex justify-end gap-3 mt-6">
                  <button
                      type="button"
                      @click="closeModal"
                      class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500"
                  >
                    취소
                  </button>
                  <button
                      type="submit"
                      :disabled="!isFormValid || isSubmitting"
                      class="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {{ isSubmitting ? '저장 중...' : '저장' }}
                  </button>
                </div>
              </form>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'

const props = defineProps({
  customer: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'save'])

const form = ref({
  name: '',
  phone: '',
  birthDate: '',
  email: '',
  postcode: '',
  address: '',
  addressDetail: '',
  note: '',
  smsAgree: false
})

const errors = ref({
  name: '',
  phone: '',
  email: ''
})

const isSubmitting = ref(false)

// 유효성 검사 규칙
const validationRules = {
  name: (value) => {
    if (!value) return '고객명을 입력해주세요.'
    if (value.length < 2) return '고객명은 2자 이상이어야 합니다.'
    return ''
  },
  phone: (value) => {
    if (!value) return '전화번호를 입력해주세요.'
    const phoneRegex = /^010-\d{4}-\d{4}$/
    if (!phoneRegex.test(value)) return '올바른 전화번호 형식이 아닙니다.'
    return ''
  },
  email: (value) => {
    if (!value) return ''
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    if (!emailRegex.test(value)) return '올바른 이메일 형식이 아닙니다.'
    return ''
  }
}

// 폼 유효성 검사
const validateField = (fieldName) => {
  if (validationRules[fieldName]) {
    errors.value[fieldName] = validationRules[fieldName](form.value[fieldName])
  }
}

const isFormValid = computed(() => {
  return !errors.value.name && !errors.value.phone && !errors.value.email &&
      form.value.name && form.value.phone
})

// 전화번호 자동 포맷팅
const formatPhoneNumber = (value) => {
  if (!value) return value
  const numbers = value.replace(/[^\d]/g, '')
  if (numbers.length <= 3) return numbers
  if (numbers.length <= 7) return `${numbers.slice(0, 3)}-${numbers.slice(3)}`
  return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`
}

// 주소 검색
const searchAddress = () => {
  // 다음 주소 검색 API 연동
  new window.daum.Postcode({
    oncomplete: (data) => {
      form.value.postcode = data.zonecode
      form.value.address = data.address
      form.value.addressDetail = ''
    }
  }).open()
}

// 폼 제출
const handleSubmit = async () => {
  try {
    isSubmitting.value = true

    // 모든 필드 유효성 검사
    Object.keys(validationRules).forEach(validateField)
    if (!isFormValid.value) return

    emit('save', form.value)
  } catch (error) {
    console.error('Failed to save customer:', error)
  } finally {
    isSubmitting.value = false
  }
}

const closeModal = () => {
  emit('close')
}

// 수정 시 기존 데이터 설정
onMounted(() => {
  if (props.customer) {
    form.value = {
      ...props.customer,
      smsAgree: props.customer.smsAgree ?? false
    }
  }
})

// 전화번호 입력 시 자동 포맷팅
const watchPhone = () => {
  const unformattedValue = form.value.phone.replace(/[^\d]/g, '')
  form.value.phone = formatPhoneNumber(unformattedValue)
}
</script>