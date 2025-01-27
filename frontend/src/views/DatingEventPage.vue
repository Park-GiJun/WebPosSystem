<template>
  <div class="min-h-screen bg-gradient-to-br from-red-50 to-green-50 flex items-center justify-center p-4">
    <div class="max-w-3xl w-full bg-white rounded-2xl shadow-xl p-8">
      <div class="flex items-center justify-center mb-6 relative">
        <div class="absolute -top-4 left-0 right-0 flex justify-between px-4">
          <span class="text-4xl">🎄</span>
          <span class="text-4xl">🎅</span>
        </div>
        <h1 class="text-3xl font-bold text-center text-red-600">
          크리스마스 사탕시그널 파티
        </h1>
      </div>

      <p class="text-center text-gray-600 mb-8">
        특별한 크리스마스 선물을 찾아보세요! 🎁
      </p>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <EventCard
            title="일시"
            icon="Calendar"
            bg-color="bg-red-50"
            icon-color="text-red-500"
        >
          <p class="text-gray-600">2024. 12. 8.(일)</p>
          <p class="text-gray-600 font-semibold">(1부) 12:45~16:40</p>
        </EventCard>

        <EventCard
            title="장소"
            icon="MapPin"
            bg-color="bg-green-50"
            icon-color="text-green-500"
        >
          <p class="text-gray-600">신촌 에비뉴53</p>
          <p class="text-sm text-gray-500">신촌역 2번출구 도보6분</p>
          <p class="text-sm text-gray-500">* 주차불가</p>
        </EventCard>

        <EventCard
            title="참가 인원"
            icon="Users"
            bg-color="bg-blue-50"
            icon-color="text-blue-500"
        >
          <p class="text-gray-600">남녀 각 15명 (총 30명)</p>
          <p class="text-gray-600">현재 {{ participants }}명 신청</p>
        </EventCard>

        <EventCard
            title="드레스코드"
            icon="Shirt"
            bg-color="bg-purple-50"
            icon-color="text-purple-500"
        >
          <p class="text-gray-600">본인의 매력을 뽐낼 수 있는 복장</p>
          <p class="text-sm text-red-500">* 후드티, 슬리퍼 금지</p>
        </EventCard>
      </div>

      <div class="bg-gray-50 rounded-xl p-6 mb-8">
        <h3 class="font-semibold text-xl mb-4 flex items-center gap-2">
          <Calendar class="w-5 h-5 text-red-500" />
          프로그램 순서
        </h3>
        <div class="space-y-4">
          <div class="border-l-4 border-red-400 pl-4">
            <h4 class="font-semibold">1️⃣ 입장 (~12:45)</h4>
            <p class="text-gray-600">호스트에게 명찰을 받고 지정된 자리에 착석</p>
          </div>

          <div class="border-l-4 border-green-400 pl-4">
            <h4 class="font-semibold">2️⃣ STAGE 1 (13:00~15:05)</h4>
            <p class="text-gray-600">3:3 릴레이 미팅 (25분마다 테이블 이동)</p>
          </div>

          <div class="border-l-4 border-blue-400 pl-4">
            <h4 class="font-semibold">3️⃣ 호감 이성 제출 (15:05~15:10)</h4>
            <p class="text-gray-600">STAGE2에서 만나고 싶은 이성 3명 선택</p>
          </div>

          <div class="border-l-4 border-purple-400 pl-4">
            <h4 class="font-semibold">4️⃣ STAGE 2 (15:30~16:30)</h4>
            <p class="text-gray-600">선호도 기반 3:3 매칭 & 사탕룸 시그널</p>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div class="bg-red-50 rounded-xl p-6">
          <h3 class="font-semibold mb-3 flex items-center gap-2">
            <Coffee class="w-5 h-5 text-red-500" />
            제공 메뉴
          </h3>
          <ul class="list-disc pl-5 text-gray-600 space-y-1">
            <li>과일, 나쵸, 치즈 등 안주</li>
            <li>칵테일, 하이볼</li>
            <li>콜라, 사이다, 주스</li>
            <li>커피 등 음료 뷔페식</li>
          </ul>
        </div>

        <div class="bg-green-50 rounded-xl p-6">
          <h3 class="font-semibold mb-3 flex items-center gap-2">
            <Gift class="w-5 h-5 text-green-500" />
            특별 이벤트
          </h3>
          <ul class="list-disc pl-5 text-gray-600 space-y-1">
            <li>사탕룸 시그널</li>
          </ul>
        </div>
      </div>

      <button
          @click="handleRegister"
          class="w-full mt-8 py-3 px-6 bg-gradient-to-r from-red-500 to-green-500 text-white rounded-xl font-semibold hover:from-red-600 hover:to-green-600 transition-all duration-200 shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
      >
        <Gift class="w-5 h-5" />
        지금 바로 신청하기
      </button>

      <p class="text-center text-sm text-gray-500 mt-4">
        * 신청 후 취소는 12월 6일 20시까지만 가능합니다 (환불 불가)
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { Calendar, Coffee, Gift } from 'lucide-vue-next';
import EventCard from '@/components/EventCard.vue';

const participants = ref(18);

const handleRegister = () => {
  participants.value++;
};
</script>