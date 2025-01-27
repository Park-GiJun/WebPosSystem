<template>
  <div class="space-y-4">
    <h4 class="font-semibold text-gray-800">프로그램 소개</h4>
    <ul class="list-disc pl-5 text-gray-600 space-y-2">
      <li v-for="(item, index) in programItems" :key="index">
        {{ item }}
      </li>
    </ul>
  </div>
</template>

<script setup>
const programItems = [
  '1:1 매칭 소개팅 (3회)',
  '아이스브레이킹 게임',
  '프리미엄 커피 & 디저트',
  '로맨틱 포토타임'
];
</script>