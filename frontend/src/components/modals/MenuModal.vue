// components/modals/MenuModal.vue
<template>
  <TransitionRoot appear :show="isOpen" as="template">
    <Dialog as="div" @close="$emit('close')" class="relative z-50">
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
            <DialogPanel class="w-full max-w-3xl bg-white rounded-2xl p-6">
              <DialogTitle class="text-lg font-medium mb-4">메뉴</DialogTitle>

              <div class="grid grid-cols-2 gap-4">
                <button
                    v-for="menu in menuItems"
                    :key="menu.id"
                    @click="handleMenuClick(menu.action)"
                    class="flex items-center gap-3 p-4 bg-gray-50 rounded-lg hover:bg-gray-100"
                >
                  <component :is="menu.icon" class="w-6 h-6" />
                  <div class="text-left">
                    <h3 class="font-medium">{{ menu.name }}</h3>
                    <p class="text-sm text-gray-500">{{ menu.description }}</p>
                  </div>
                </button>
              </div>

              <button
                  @click="$emit('close')"
                  class="mt-6 w-full px-4 py-2 bg-gray-100 rounded-lg hover:bg-gray-200"
              >
                닫기
              </button>
            </DialogPanel>
          </TransitionChild>
        </div>
      </div>
    </Dialog>
  </TransitionRoot>
</template>

<script setup>
import {
  TransitionRoot,
  TransitionChild,
  Dialog,
  DialogPanel,
  DialogTitle,
} from '@headlessui/vue'

defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  menuItems: {
    type: Array,
    required: true
  }
})

const emit = defineEmits(['close', 'menu-click'])

const handleMenuClick = (action) => {
  emit('menu-click', action)
}
</script>